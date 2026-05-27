package com.octopus.demo.common.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.junit.jupiter.api.Assertions.*;

class JwtAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(JwtAutoConfiguration.class);

    @Test
    void autoConfiguration_createsJwtUtilBean() {
        contextRunner.run(context -> {
            assertTrue(context.containsBean("jwtUtil"), "JwtUtil bean should be registered");
            JwtUtil util = context.getBean(JwtUtil.class);
            String token = util.generateToken(1L);
            assertEquals(1L, util.parseToken(token));
        });
    }

    @Test
    void autoConfiguration_withoutSecretKey_createsWorkingJwtUtil() {
        contextRunner.run(context -> {
            assertTrue(context.containsBean("jwtUtil"));
            JwtUtil util = context.getBean(JwtUtil.class);
            assertNotNull(util);
        });
    }

    @Test
    void autoConfiguration_withSecretKey_usesConfiguredKey() {
        String key = JwtUtil.generateRandomSecretKeyString();
        contextRunner
                .withPropertyValues("octopus.jwt.secret-key=" + key)
                .run(context -> {
                    assertTrue(context.containsBean("jwtUtil"));
                    JwtUtil util = context.getBean(JwtUtil.class);
                    String token = util.generateToken(1L);
                    assertEquals(1L, util.parseToken(token));
                });
    }

    @Test
    void autoConfiguration_withExpirationDays_override() {
        String key = JwtUtil.generateRandomSecretKeyString();
        contextRunner
                .withPropertyValues(
                        "octopus.jwt.secret-key=" + key,
                        "octopus.jwt.expiration-days=7")
                .run(context -> {
                    assertTrue(context.containsBean("jwtUtil"));
                });
    }

    @Test
    void autoConfiguration_registersJwtPropertiesBean() {
        contextRunner.run(context -> {
            assertNotNull(context.getBean(JwtProperties.class),
                       "JwtProperties bean should be registered via EnableConfigurationProperties");
        });
    }
}