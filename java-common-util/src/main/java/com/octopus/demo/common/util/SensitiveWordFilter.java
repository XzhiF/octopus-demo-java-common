package com.octopus.demo.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 敏感词过滤工具：内置「辱骂/色情」默认词库（classpath 资源 sensitive-words.txt），
 * Trie(DFA) 字符树 O(n) 扫描，提供 {@link #detect(String)} 与 {@link #mask(String)}。
 * 匹配前做归一化（ASCII 大小写折叠 + 全角转半角 + 干扰符跳过），命中位置映射回原文。
 * 构建后不可变。
 *
 * <pre>{@code
 * SensitiveWordFilter filter = SensitiveWordFilter.builder().build();
 * List<SensitiveWordHit> hits = filter.detect("你个傻逼");
 * String masked = filter.mask("你个傻逼"); // 你个**
 * }</pre>
 */
public final class SensitiveWordFilter {

    private static final String DEFAULT_LEXICON = "sensitive-words.txt";

    /** Trie 根节点；构建后只读。 */
    private final Node root;

    private SensitiveWordFilter(Map<String, SensitiveWordCategory> lexicon) {
        this.root = new Node();
        for (Map.Entry<String, SensitiveWordCategory> e : lexicon.entrySet()) {
            // 构建期：词表词归一化后进 Trie
            String word = normalizeWord(e.getKey());
            if (word.isEmpty()) {
                continue;
            }
            Node cur = this.root;
            for (int i = 0; i < word.length(); i++) {
                cur = cur.children.computeIfAbsent(word.charAt(i), c -> new Node());
            }
            cur.category = e.getValue();
            cur.word = word;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 扫描文本中的敏感词。
     *
     * @param text 待检测文本，null 视为无命中
     * @return 命中列表（按原文位置升序），永不为 null
     */
    public List<SensitiveWordHit> detect(String text) {
        List<SensitiveWordHit> hits = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return hits;
        }
        // 运行期归一化：折叠大小写/全半角、剥干扰符；pos 把命中段映射回原文（含中间干扰符）
        final int n = text.length();
        char[] eff = new char[n];
        int[] pos = new int[n];
        int m = 0;
        for (int i = 0; i < n; i++) {
            char c = text.charAt(i);
            if (isInterference(c)) {
                continue;
            }
            eff[m] = normalize(c);
            pos[m] = i;
            m++;
        }
        int i = 0;
        while (i < m) {
            Node cur = root;
            int bestEnd = -1;
            String bestWord = null;
            SensitiveWordCategory bestCat = null;
            for (int j = i; j < m; j++) {
                cur = cur.children.get(eff[j]);
                if (cur == null) {
                    break;
                }
                if (cur.category != null) {
                    bestEnd = j;
                    bestWord = cur.word;
                    bestCat = cur.category;
                }
            }
            if (bestEnd >= 0) {
                // ponytail: 同起点取最长匹配后跳过命中段；重叠词（傻逼/逼样）后者不报，需全报时再改回溯
                hits.add(new SensitiveWordHit(bestWord, bestCat, pos[i], pos[bestEnd]));
                i = bestEnd + 1;
            } else {
                i++;
            }
        }
        return hits;
    }

    /**
     * 将命中段逐字符替换为等长 {@code *}。
     *
     * @param text 待掩码文本
     * @return 与原文等长的掩码文本；null 原样返回
     */
    public String mask(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        char[] chars = text.toCharArray();
        for (SensitiveWordHit hit : detect(text)) {
            for (int i = hit.startIndex(); i <= hit.endIndex(); i++) {
                chars[i] = '*';
            }
        }
        return new String(chars);
    }

    /**
     * 解析行式词库（{@code 类别:词}，{@code #} 注释）。
     * 非法行（无冒号/空类别/空词/未知类别）跳过，不中断构建。
     */
    static Map<String, SensitiveWordCategory> loadLexicon(InputStream in) throws IOException {
        Map<String, SensitiveWordCategory> lexicon = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.charAt(0) == '#') {
                    continue;
                }
                int sep = line.indexOf(':');
                if (sep <= 0 || sep == line.length() - 1) {
                    continue;
                }
                SensitiveWordCategory category;
                try {
                    category = SensitiveWordCategory.valueOf(line.substring(0, sep).trim().toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    continue;
                }
                lexicon.put(line.substring(sep + 1).trim(), category);
            }
        }
        return lexicon;
    }

    /**
     * 干扰符判定（单点可调旋钮，KD4）：ASCII 控制/空白 + Unicode 标点/符号/空白类跳过；
     * 常规汉字、字母、数字不跳——过杀正常文本比漏杀更致命。
     */
    private static boolean isInterference(char c) {
        if (c <= ' ') {
            return true;
        }
        return switch (Character.getType(c)) {
            case Character.CONNECTOR_PUNCTUATION, Character.DASH_PUNCTUATION, Character.START_PUNCTUATION,
                    Character.END_PUNCTUATION, Character.INITIAL_QUOTE_PUNCTUATION, Character.FINAL_QUOTE_PUNCTUATION,
                    Character.OTHER_PUNCTUATION, Character.MATH_SYMBOL, Character.CURRENCY_SYMBOL,
                    Character.MODIFIER_SYMBOL, Character.OTHER_SYMBOL, Character.SPACE_SEPARATOR,
                    Character.LINE_SEPARATOR, Character.PARAGRAPH_SEPARATOR -> true;
            default -> false;
        };
    }

    /** 单字符归一：ASCII 大写折小写；全角 ！～（FF01-FF5E）转半角后同折。 */
    private static char normalize(char c) {
        if (c >= 'A' && c <= 'Z') {
            return (char) (c + 32);
        }
        if (c >= '！' && c <= '～') {
            char h = (char) (c - 0xFEE0);
            return h >= 'A' && h <= 'Z' ? (char) (h + 32) : h;
        }
        return c;
    }

    private static String normalizeWord(String word) {
        char[] buf = new char[word.length()];
        int m = 0;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (!isInterference(c)) {
                buf[m++] = normalize(c);
            }
        }
        return new String(buf, 0, m);
    }

    private static final class Node {
        final Map<Character, Node> children = new HashMap<>();
        SensitiveWordCategory category;
        String word;
    }

    /** 构建器：默认词库之上支持 append/override 自定义词库，构建后不可变（KD5/8）。 */
    public static final class Builder {

        /** null = 用默认词库；overrideWords 后仅自定义底表。 */
        private Map<String, SensitiveWordCategory> base;
        /** 追加词，build 时最后写入 → 跨类别冲突自定义覆盖默认（KD10）。 */
        private final Map<String, SensitiveWordCategory> appended = new LinkedHashMap<>();

        private Builder() {
        }

        /**
         * 在默认词库上追加业务专属违禁词（US7），多次调用叠加；null/空白词静默忽略。
         */
        public Builder appendWords(SensitiveWordCategory category, Collection<String> words) {
            if (category != null && words != null) {
                for (String w : words) {
                    if (w != null && !w.isBlank()) {
                        appended.put(w.trim(), category);
                    }
                }
            }
            return this;
        }

        /**
         * 完全替换默认词库（US8），实例只认传入词表；null/空白词静默忽略。
         */
        public Builder overrideWords(Collection<WordEntry> entries) {
            Map<String, SensitiveWordCategory> replaced = new LinkedHashMap<>();
            if (entries != null) {
                for (WordEntry e : entries) {
                    if (e != null && e.word() != null && !e.word().isBlank() && e.category() != null) {
                        replaced.put(e.word().trim(), e.category());
                    }
                }
            }
            base = replaced;
            return this;
        }

        public SensitiveWordFilter build() {
            Map<String, SensitiveWordCategory> lexicon = base != null ? base : loadDefaultLexicon();
            // 合并语义：append = 默认∪自定义去重；同词后写覆盖 → 自定义类别优先
            lexicon.putAll(appended);
            return new SensitiveWordFilter(lexicon);
        }

        private static Map<String, SensitiveWordCategory> loadDefaultLexicon() {
            try (InputStream in = SensitiveWordFilter.class.getClassLoader().getResourceAsStream(DEFAULT_LEXICON)) {
                if (in == null) {
                    throw new IllegalStateException("默认词库资源缺失: " + DEFAULT_LEXICON);
                }
                return loadLexicon(in);
            } catch (IOException e) {
                throw new IllegalStateException("默认词库加载失败: " + DEFAULT_LEXICON, e);
            }
        }
    }
}
