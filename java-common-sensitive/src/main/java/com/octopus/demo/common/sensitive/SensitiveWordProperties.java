package com.octopus.demo.common.sensitive;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

/**
 * Configuration for the sensitive word filter ({@code octopus.sensitive.*}).
 */
@ConfigurationProperties(prefix = "octopus.sensitive")
public class SensitiveWordProperties {

    /** When {@code false}, a no-op filter bean is registered instead. */
    private boolean enabled = true;

    /** Optional external dictionary location, e.g. {@code classpath:sensitive/custom.txt}. */
    private Resource externalDictionary;

    /** How the external dictionary combines with the built-in ones. */
    private ExternalDictionaryMode externalDictionaryMode = ExternalDictionaryMode.APPEND;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Resource getExternalDictionary() {
        return externalDictionary;
    }

    public void setExternalDictionary(Resource externalDictionary) {
        this.externalDictionary = externalDictionary;
    }

    public ExternalDictionaryMode getExternalDictionaryMode() {
        return externalDictionaryMode;
    }

    public void setExternalDictionaryMode(ExternalDictionaryMode externalDictionaryMode) {
        this.externalDictionaryMode = externalDictionaryMode;
    }
}
