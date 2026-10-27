# java-common-sensitive

Reusable sensitive-word filter for octopus-demo services. It ships built-in + external
dictionaries, text normalization, substring matching, and Spring Boot auto-configuration so a
downstream service can reject disallowed text (for example a user nickname) before persisting
it, without exposing dictionary contents to callers.

This module intentionally lives outside `java-common-util` (which stays a pure static-utility
module): it carries an auto-configuration, a `@ConfigurationProperties` type and classpath
resources, which matches the shape of the `auth` / `health` modules.

## Auto-configuration

`SensitiveAutoConfiguration` is declared in
`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` and
registers a `SensitiveWordFilter` bean. Downstream code injects the interface:

```java
SensitiveCheckResult result = sensitiveWordFilter.check(nickname);
if (result.hit()) {
    // reject; log only result.category() — never the input text or matched word
}
```

`SensitiveCheckResult` is `(boolean hit, SensitiveCategory category)`; `category` is `null`
when `hit` is `false`. Categories are `ABUSE` and `PORN`.

## Configuration (`octopus.sensitive.*`)

```yaml
octopus:
  sensitive:
    enabled: true
    # optional: classpath:sensitive/custom.txt or file:/etc/octopus/sensitive.txt
    external-dictionary:
    external-dictionary-mode: append   # append | replace
```

| Property | Default | Meaning |
|----------|---------|---------|
| `enabled` | `true` | `false` registers a no-op filter that never hits (bean still exists). |
| `external-dictionary` | none | Optional Spring `Resource` location of an extra dictionary. |
| `external-dictionary-mode` | `append` | `append` merges with built-in entries; `replace` uses only external entries. |

### `enabled` switch semantics

- `enabled=true` (default): built-in dictionaries (plus an external dictionary, if configured)
  are active.
- `enabled=false`: the auto-configuration still registers a `SensitiveWordFilter` bean, but it
  is a no-op whose `check` always returns not-hit. Injection points keep working; sensitive-word
  rejection is simply turned off. This is a deliberate degradation, not a startup failure.

### External dictionary modes

- `append`: built-in entries stay active and external entries are merged in.
- `replace`: built-in entries are discarded; only the external dictionary is used.

An external dictionary that cannot be read, contains a malformed line or an unknown category,
or yields no valid entries fails application startup (fail-fast) with the source and line
number. Silent fallback is never used.

## Dictionary format

UTF-8 text, one entry per line, `类别|词` (category `ABUSE` or `PORN`, case-insensitive).
Blank lines and lines starting with `#` are ignored. Malformed lines, unknown categories,
and empty words fail application startup with the file and line number.

Built-in dictionaries: `classpath:sensitive/abuse.txt`, `classpath:sensitive/porn.txt`,
each curated to 20–30 unique entries (~demo scale).

### Curation rules

- Keep each category at 20–30 entries, no duplicates within or across the two files.
- English entries must be at least 4 characters after normalization; never add very short
  generic substrings (for example `ass`) that would match inside `class` / `assignment`.
- Avoid common words whose removal would cause mass false positives (`fool`, `wretch`, …).
- The false-positive guards in `FalsePositiveGuardTest` (`class`, `assignment`, `classic`,
  `passenger`, `Scunthorpe`, plus ordinary Chinese/English nicknames) must keep passing.

## Normalization and matching

Input and dictionary entries are normalized the same way:
NFKC → lowercase (`Locale.ROOT`) → remove Unicode whitespace, punctuation (`\p{P}`) and
symbols (`\p{S}`). Matching is a plain substring check over the normalized text, longest
entry first for stable category reporting. This covers case changes, full-width/half-width
variants, and inserted whitespace or punctuation (`蠢 货`, `蠢，货`, `b.a.s.t.a.r.d`,
`ＢＡＳＴＡＲＤ`).

`null`, empty, blank and separator-only input returns a not-hit result without throwing;
required-field validation stays the caller's responsibility (for example `@NotBlank`).

### Known limitations

- Zero-width characters (`U+200B`, category `Cf`) are **not** removed by normalization, so
  inserting one inside a word bypasses matching. This is an accepted MVP limitation and is
  pinned by `NormalizationEdgeTest`.
- No pinyin, homophone, homoglyph, character-splitting or leetspeak resistance; those are
  explicitly out of scope for this phase.
- Matching is plain substring scan (dictionary is small); no Aho–Corasick automaton.
- The filter only reports *whether* text hit and *which category*; it does not return the
  matched word.

## Logging convention

When rejecting input based on a hit, log the **category and field name only**. Never log the
raw input text or the matched word; the response must not expose dictionary contents.
