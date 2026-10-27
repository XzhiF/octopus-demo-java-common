package com.octopus.demo.common.sensitive;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

/**
 * Registers a {@link SensitiveWordFilter} bean. When {@code octopus.sensitive.enabled=false}
 * the registered bean is a no-op so downstream injection still succeeds.
 */
@AutoConfiguration
@EnableConfigurationProperties(SensitiveWordProperties.class)
public class SensitiveAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SensitiveWordFilter sensitiveWordFilter(SensitiveWordProperties properties) {
        if (!properties.isEnabled()) {
            return NoOpSensitiveWordFilter.INSTANCE;
        }
        return new DefaultSensitiveWordFilter(loadWords(properties));
    }

    static Map<String, SensitiveCategory> loadWords(SensitiveWordProperties properties) {
        Map<String, SensitiveCategory> words = new LinkedHashMap<>();
        Resource external = properties.getExternalDictionary();
        if (properties.getExternalDictionaryMode() != ExternalDictionaryMode.REPLACE) {
            words.putAll(SensitiveDictionaryLoader.loadBuiltIn());
        }
        if (external != null) {
            Map<String, SensitiveCategory> externalWords = SensitiveDictionaryLoader.loadExternal(external);
            if (externalWords.isEmpty()) {
                throw new IllegalStateException(
                        "External sensitive dictionary contains no valid entries: " + external);
            }
            words.putAll(externalWords);
        }
        if (words.isEmpty()) {
            throw new IllegalStateException(
                    "No sensitive dictionary entries loaded; check octopus.sensitive configuration");
        }
        return words;
    }
}
