package com.octopus.demo.common.sensitive;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

@DisplayName("Sensitive external dictionary")
class SensitiveExternalDictionaryTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SensitiveAutoConfiguration.class));

    @TempDir
    Path tempDir;

    private Path writeDictionary(String fileName, String content) throws IOException {
        Path file = tempDir.resolve(fileName);
        Files.writeString(file, content, StandardCharsets.UTF_8);
        return file;
    }

    private static String location(Path file) {
        return file.toUri().toString();
    }

    @Test
    @DisplayName("append mode: external entries hit and built-in entries keep hitting")
    void appendMergesExternalAndBuiltIn() throws IOException {
        Path file = writeDictionary("append.txt", "ABUSE|测试辱骂\nPORN|测试色情\n");

        contextRunner
                .withPropertyValues(
                        "octopus.sensitive.external-dictionary=" + location(file),
                        "octopus.sensitive.external-dictionary-mode=append")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    SensitiveWordFilter filter = context.getBean(SensitiveWordFilter.class);
                    assertThat(filter.check("测试辱骂").category()).isEqualTo(SensitiveCategory.ABUSE);
                    assertThat(filter.check("测试色情").category()).isEqualTo(SensitiveCategory.PORN);
                    assertThat(filter.check("bastard").hit()).isTrue();
                    assertThat(filter.check("E2E_小明").hit()).isFalse();
                });
    }

    @Test
    @DisplayName("append is the default mode when external-dictionary-mode is omitted")
    void appendIsDefaultMode() throws IOException {
        Path file = writeDictionary("default-append.txt", "ABUSE|默认追加词\n");

        contextRunner
                .withPropertyValues("octopus.sensitive.external-dictionary=" + location(file))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    SensitiveWordFilter filter = context.getBean(SensitiveWordFilter.class);
                    assertThat(filter.check("默认追加词").hit()).isTrue();
                    assertThat(filter.check("bastard").hit()).isTrue();
                });
    }

    @Test
    @DisplayName("replace mode: only external entries are used")
    void replaceDiscardsBuiltIn() throws IOException {
        Path file = writeDictionary("replace.txt", "ABUSE|替换辱骂\nPORN|替换色情\n");

        contextRunner
                .withPropertyValues(
                        "octopus.sensitive.external-dictionary=" + location(file),
                        "octopus.sensitive.external-dictionary-mode=replace")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    SensitiveWordFilter filter = context.getBean(SensitiveWordFilter.class);
                    assertThat(filter.check("替换辱骂").category()).isEqualTo(SensitiveCategory.ABUSE);
                    assertThat(filter.check("替换色情").category()).isEqualTo(SensitiveCategory.PORN);
                    assertThat(filter.check("bastard").hit()).isFalse();
                    assertThat(filter.check("蠢货").hit()).isFalse();
                });
    }

    @Test
    @DisplayName("external entries are normalized like built-in entries")
    void externalEntriesUseSameNormalization() throws IOException {
        Path file = writeDictionary("normalized.txt", "ABUSE|BadWordDemo\n");

        contextRunner
                .withPropertyValues("octopus.sensitive.external-dictionary=" + location(file))
                .run(context -> {
                    SensitiveWordFilter filter = context.getBean(SensitiveWordFilter.class);
                    assertThat(filter.check("ＢＡＤ ＷＯＲＤ ＤＥＭＯ").hit()).isTrue();
                    assertThat(filter.check("bad word demo").hit()).isTrue();
                    assertThat(filter.check("b-a-d-w-o-r-d-d-e-m-o").hit()).isTrue();
                });
    }

    @Test
    @DisplayName("unreadable external dictionary fails startup with the file location")
    void unreadableExternalDictionaryFailsStartup() {
        Path missing = tempDir.resolve("missing.txt");

        contextRunner
                .withPropertyValues("octopus.sensitive.external-dictionary=" + location(missing))
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .hasStackTraceContaining("missing.txt");
                });
    }

    @Test
    @DisplayName("invalid category fails startup with file name and line number")
    void invalidCategoryFailsStartupWithLineNumber() throws IOException {
        Path file = writeDictionary("bad-category.txt", "# comment\nABUSE|ok\nNOT_A_CATEGORY|bad\n");

        contextRunner
                .withPropertyValues("octopus.sensitive.external-dictionary=" + location(file))
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .hasStackTraceContaining("bad-category.txt")
                            .hasStackTraceContaining(":3: NOT_A_CATEGORY|bad");
                });
    }

    @Test
    @DisplayName("malformed line fails startup with file name and line number")
    void malformedLineFailsStartupWithLineNumber() throws IOException {
        Path file = writeDictionary("bad-format.txt", "ABUSE|ok\nbroken-line\n");

        contextRunner
                .withPropertyValues("octopus.sensitive.external-dictionary=" + location(file))
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .hasStackTraceContaining("bad-format.txt")
                            .hasStackTraceContaining(":2: broken-line");
                });
    }

    @Test
    @DisplayName("external dictionary without valid entries fails startup")
    void emptyExternalDictionaryFailsStartup() throws IOException {
        Path file = writeDictionary("empty.txt", "# nothing here\n\n");

        contextRunner
                .withPropertyValues(
                        "octopus.sensitive.external-dictionary=" + location(file),
                        "octopus.sensitive.external-dictionary-mode=replace")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .hasStackTraceContaining("empty.txt")
                            .hasStackTraceContaining("no valid entries");
                });
    }

    @Test
    @DisplayName("without external configuration built-in dictionaries behave unchanged")
    void noExternalConfigurationKeepsBuiltInBehavior() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();
            SensitiveWordFilter filter = context.getBean(SensitiveWordFilter.class);
            assertThat(filter.check("bastard").category()).isEqualTo(SensitiveCategory.ABUSE);
            assertThat(filter.check("porn").category()).isEqualTo(SensitiveCategory.PORN);
            assertThat(filter.check("E2E_小明").hit()).isFalse();
        });
    }
}
