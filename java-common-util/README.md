# java-common-util

xzf 组织公共工具模块，提供 JWT / AES / MD5 / 高亮 / 敏感信息脱敏等零依赖静态工具类。

## 敏感信息脱敏

用于日志输出与界面回显时对手机号、邮箱打码（`com.octopus.demo.common.util.MaskUtils`）。

```java
// 手机号：保留前 3 后 4
MaskUtils.maskPhone("13812345678");  // -> 138****5678

// 邮箱：本地部分保留首尾字符，域名不变
MaskUtils.maskEmail("alice@example.com");  // -> a***e@example.com
```

约定：非法或异常输入（`null`、空串、手机号长度不符、邮箱缺 `@`）一律原样返回（`null` 返回 `null`），脱敏绝不抛异常，保证日志路径零风险。

## 自由文本脱敏 `maskText`

当敏感信息夹在日志行、接口回显或异常消息里时，无需事先拆分，直接整段交给 `MaskUtils.maskText`：

```java
// 六类全开
String masked = MaskUtils.maskText(text);

// 只脱敏指定类型；null 或空集合均视为“全开”
String onlyPhoneAndId = MaskUtils.maskText(text, Set.of(MaskType.PHONE, MaskType.ID_CARD));
```

单类示例（与 `MaskUtilsTest` 断言逐条一致）：

```java
MaskUtils.maskText("联系 13812345678");            // -> 联系 138****5678
MaskUtils.maskText("alice@example.com");           // -> a***e@example.com
MaskUtils.maskText("11010519491231002X");          // -> 110105********002X
MaskUtils.maskText("6222021234567890123");         // -> ***************0123
MaskUtils.maskText("收件人：张三");                 // -> 收件人：张*
MaskUtils.maskText("地址：北京市朝阳区建国路88号");  // -> 地址：北京市朝阳区****
```

### 识别规则

| MaskType | 识别口径 | 掩码形态 |
|---|---|---|
| `PHONE` | 前后不贴数字的 `1[3-9]\d{9}` | 前 3 + `****` + 后 4 |
| `EMAIL` | 常规邮箱（含 `.` `-` `_` `%` `+`，域名至少一个点） | 本地部分保首尾、中间 `***`；本地部分 ≤2 整体 `***`，域名不变 |
| `ID_CARD` | 18 位且通过 ISO 7064 MOD 11-2 校验位（末位可为 `X`/`x`） | 前 6 + 中间 8 位 `*` + 后 4 |
| `BANK_CARD` | 13–19 位连续数字，前后不贴数字，且未被身份证占用 | 仅保留后 4 位，其余按原长度替换为 `*` |
| `NAME` | `姓名`/`收件人`/`联系人`/`收货人` 标签锚定的 2–4 个汉字（可含 `·`） | 保留首字，其余逐字 `*` |
| `ADDRESS` | 行政区划链 + 60 字符内详细地址特征（`路/街/道/巷/号/楼/室/栋/小区/大厦/园区/村/镇`） | 保留到最后一个区县级单位，其后整体替换为 `****` |

重叠区间按“起点升序、长度降序”贪心仲裁，任一字符最多打码一次，长匹配优先。

### 约定与边界

- **fail-open**：`null` 或空串原样返回；任一规则抛 `RuntimeException` 时吞掉并跳过该规则，其余规则照常；`maskText` 绝不抛异常。
- **幂等**：对已脱敏结果再次调用 `maskText`，输出与第一次完全一致。
- **不识别清单**（刻意漏码，宁可不误伤）：
  - 无标签的姓名，如 `张三今天没来`
  - 带空格或横线分组的卡号，如 `6222 0212 3456 7890`
  - 15 位旧版身份证
- 未命中的字符（含标点、换行、空白）逐字原样保留。

