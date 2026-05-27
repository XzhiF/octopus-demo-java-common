package com.octopus.demo.common.auth;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Auto-configuration that registers AuthInterceptor for all paths.
 * The interceptor only acts on methods/classes annotated with @RequireAuth.
 */
@AutoConfiguration
public class AuthAutoConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/**");
    }
}
