package com.octopus.demo.common.audit;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration
public class AuditAutoConfiguration implements WebMvcConfigurer {

    @Bean
    @ConditionalOnMissingBean(AuditLogger.class)
    public AuditLogger auditLogger() {
        return new InMemoryAuditLogger();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuditInterceptor())
            .addPathPatterns("/api/**");
    }
}
