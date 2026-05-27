package com.octopus.demo.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration that creates JwtUtil bean from JwtProperties.
 * If secret-key is not configured, generates a random key and logs a warning.
 */
@AutoConfiguration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(JwtAutoConfiguration.class);

    private final JwtProperties jwtProperties;

    public JwtAutoConfiguration(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Bean
    @ConditionalOnMissingBean(JwtUtil.class)
    public JwtUtil jwtUtil() {
        String secretKey = jwtProperties.getSecretKey();
        if (secretKey == null || secretKey.isEmpty()) {
            secretKey = JwtUtil.generateRandomSecretKeyString();
            log.warn("JWT secret key not configured, using random key. "
                     + "Configure 'octopus.jwt.secret-key' for production.");
        }
        return JwtUtil.create(secretKey, jwtProperties.getExpirationDays());
    }
}