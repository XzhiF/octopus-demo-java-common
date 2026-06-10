package com.octopus.demo.common.audit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuditAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(AuditAutoConfiguration.class));

    @Test
    @DisplayName("registers InMemoryAuditLogger by default")
    void registersDefaultLogger() {
        contextRunner.run(context -> {
            assertTrue(context.containsBean("auditLogger"));
            assertInstanceOf(InMemoryAuditLogger.class, context.getBean(AuditLogger.class));
        });
    }

    @Test
    @DisplayName("custom AuditLogger bean takes precedence")
    void customBeanTakesPrecedence() {
        contextRunner
            .withUserConfiguration(CustomAuditLoggerConfig.class)
            .run(context -> {
                AuditLogger logger = context.getBean(AuditLogger.class);
                assertFalse(logger instanceof InMemoryAuditLogger);
            });
    }

    @Configuration
    static class CustomAuditLoggerConfig {
        @Bean
        public AuditLogger auditLogger() {
            return new AuditLogger() {
                public void log(AuditEvent event) {}
                public List<AuditEvent> query(AuditQuery query) { return List.of(); }
                public List<AuditEvent> findAll() { return List.of(); }
            };
        }
    }
}
