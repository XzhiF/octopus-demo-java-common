package com.octopus.demo.common.auth;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Auto-configuration that registers AuthInterceptor for all paths.
 * The interceptor only acts on methods/classes annotated with @RequireAuth.
 * Custom AuthInterceptor beans defined by downstream applications take precedence.
 */
@AutoConfiguration
public class AuthAutoConfiguration implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public AuthAutoConfiguration(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthInterceptor authInterceptor() {
        return new AuthInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**");
    }
}
