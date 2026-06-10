package com.octopus.demo.common.audit;

import com.octopus.demo.common.auth.AuthAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration(after = AuthAutoConfiguration.class)
@ConditionalOnProperty(prefix = "octopus.audit", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AuditAutoConfiguration implements WebMvcConfigurer {

    @Bean
    @ConditionalOnMissingBean(AuditLogger.class)
    public AuditLogger auditLogger() {
        return new InMemoryAuditLogger();
    }

    @Bean
    @ConditionalOnMissingBean(AuditInterceptor.class)
    public AuditInterceptor auditInterceptor() {
        return new AuditInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditInterceptor())
            .addPathPatterns("/api/**");
    }
}
