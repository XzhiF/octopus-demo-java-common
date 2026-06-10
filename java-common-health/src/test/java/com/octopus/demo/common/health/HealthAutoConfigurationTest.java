package com.octopus.demo.common.health;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HealthAutoConfiguration")
class HealthAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(HealthAutoConfiguration.class));

    @Test
    @DisplayName("registers HealthController bean by default")
    void defaultContext_registersHealthController() {
        contextRunner.run(context ->
                assertThat(context).hasSingleBean(HealthController.class));
    }

    @Test
    @DisplayName("does not register HealthController when octopus.health.enabled=false")
    void disabledByProperty_doesNotRegisterBean() {
        contextRunner
                .withPropertyValues("octopus.health.enabled=false")
                .run(context ->
                        assertThat(context).doesNotHaveBean(HealthController.class));
    }

    @Test
    @DisplayName("does not register HealthController when custom bean is provided")
    void customBeanProvided_doesNotRegisterDefault() {
        contextRunner
                .withUserConfiguration(CustomHealthConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(HealthController.class);
                    assertThat(context.getBean(HealthController.class))
                            .isSameAs(CustomHealthConfig.customController);
                });
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomHealthConfig {
        static final HealthController customController = new HealthController();

        @Bean
        HealthController healthController() {
            return customController;
        }
    }
}
