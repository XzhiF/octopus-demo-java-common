package com.octopus.demo.common.health;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot auto-configuration that registers HealthController.
 * <p>
 * Can be disabled via {@code octopus.health.enabled=false}.
 * Unlike AuthAutoConfiguration, this provides a property switch because
 * health endpoints may need to be fully disabled in specific environments
 * (e.g., load testing, or when Actuator replaces this lightweight probe).
 * <p>
 * Downstream apps can override by providing their own HealthController bean.
 * Independent of AuthAutoConfiguration — no ordering dependency required.
 * <p>
 * Path conflict note: if a downstream app maps a different controller class
 * to {@code /health}, it should either extend HealthController or set
 * {@code octopus.health.enabled=false} to avoid ambiguous handler errors.
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "octopus.health", name = "enabled", havingValue = "true", matchIfMissing = true)
public class HealthAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public HealthController healthController() {
        return new HealthController();
    }
}
