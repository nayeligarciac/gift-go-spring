package com.giftandgo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "feature-flags")
public class FeatureFlagProperties {

    private boolean enableValidations;

    public boolean isEnableValidations() {
        return enableValidations;
    }

    public FeatureFlagProperties setEnableValidations(boolean enableValidations) {
        this.enableValidations = enableValidations;
        return this;
    }
}
