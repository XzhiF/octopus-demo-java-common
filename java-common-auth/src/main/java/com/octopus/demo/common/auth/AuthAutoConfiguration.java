package com.octopus.demo.common.auth;

import org.springframework.beans.factory.ObjectProvider;
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

    private final ObjectProvider<AuthInterceptor> authInterceptorProvider;

    public AuthAutoConfiguration(ObjectProvider<AuthInterceptor> authInterceptorProvider) {
        this.authInterceptorProvider = authInterceptorProvider;
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthInterceptor authInterceptor() {
        return new AuthInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptorProvider.getObject())
                .addPathPatterns("/**");
    }
}
