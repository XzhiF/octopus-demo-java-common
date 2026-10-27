package com.octopus.demo.common.sensitive;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

@DisplayName("SensitiveAutoConfiguration")
class SensitiveAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SensitiveAutoConfiguration.class));

    @Test
    @DisplayName("registers a working filter by default")
    void defaultRegistersDefaultFilter() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(SensitiveWordFilter.class);
            SensitiveWordFilter filter = context.getBean(SensitiveWordFilter.class);
            assertThat(filter.check("bastard").hit()).isTrue();
            assertThat(filter.check("E2E_小明").hit()).isFalse();
        });
    }

    @Test
    @DisplayName("registers a no-op filter when octopus.sensitive.enabled=false")
    void disabledRegistersNoOpFilter() {
        contextRunner
                .withPropertyValues("octopus.sensitive.enabled=false")
                .run(context -> {
                    assertThat(context).hasSingleBean(SensitiveWordFilter.class);
                    SensitiveWordFilter filter = context.getBean(SensitiveWordFilter.class);
                    assertThat(filter).isSameAs(NoOpSensitiveWordFilter.INSTANCE);
                    assertThat(filter.check("bastard").hit()).isFalse();
                });
    }

    @Test
    @DisplayName("append mode merges external entries with built-in entries")
    void appendModeMergesExternalDictionary() {
        contextRunner
                .withPropertyValues(
                        "octopus.sensitive.external-dictionary=classpath:sensitive/external-append.txt",
                        "octopus.sensitive.external-dictionary-mode=append")
                .run(context -> {
                    SensitiveWordFilter filter = context.getBean(SensitiveWordFilter.class);
                    assertThat(filter.check("测试脏词").category()).isEqualTo(SensitiveCategory.ABUSE);
                    assertThat(filter.check("测试色情").category()).isEqualTo(SensitiveCategory.PORN);
                    assertThat(filter.check("bastard").hit()).isTrue();
                });
    }

    @Test
    @DisplayName("replace mode discards built-in entries")
    void replaceModeDiscardsBuiltInDictionary() {
        contextRunner
                .withPropertyValues(
                        "octopus.sensitive.external-dictionary=classpath:sensitive/external-replace.txt",
                        "octopus.sensitive.external-dictionary-mode=replace")
                .run(context -> {
                    SensitiveWordFilter filter = context.getBean(SensitiveWordFilter.class);
                    assertThat(filter.check("自定义辱骂").category()).isEqualTo(SensitiveCategory.ABUSE);
                    assertThat(filter.check("bastard").hit()).isFalse();
                });
    }

    @Test
    @DisplayName("fails fast when the external dictionary has an invalid line")
    void invalidExternalDictionaryFailsStartup() {
        contextRunner
                .withPropertyValues(
                        "octopus.sensitive.external-dictionary=classpath:sensitive/external-invalid.txt")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .hasStackTraceContaining("external-invalid.txt]")
                            .hasStackTraceContaining(":3:");
                });
    }

    @Test
    @DisplayName("fails fast when the external dictionary has no valid entries")
    void emptyExternalDictionaryFailsStartup() {
        contextRunner
                .withPropertyValues(
                        "octopus.sensitive.external-dictionary=classpath:sensitive/external-empty.txt",
                        "octopus.sensitive.external-dictionary-mode=replace")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .hasStackTraceContaining("no valid entries");
                });
    }
}
