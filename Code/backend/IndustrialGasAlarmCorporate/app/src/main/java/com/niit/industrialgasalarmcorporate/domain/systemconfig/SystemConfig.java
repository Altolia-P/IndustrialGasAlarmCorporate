package com.niit.industrialgasalarmcorporate.domain.systemconfig;

import java.time.LocalDateTime;

public class SystemConfig {

    private final String configKey;
    private String configValue;
    private String description;
    private final int version;
    private final LocalDateTime updatedAt;

    public SystemConfig(String configKey, String configValue, String description) {
        this.configKey = configKey;
        this.configValue = configValue;
        this.description = description;
        this.version = 0;
        this.updatedAt = LocalDateTime.now();
    }

    public SystemConfig(String configKey, String configValue, String description, int version,
                        LocalDateTime updatedAt) {
        this.configKey = configKey;
        this.configValue = configValue;
        this.description = description;
        this.version = version;
        this.updatedAt = updatedAt;
    }

    public void update(String configValue, String description) {
        this.configValue = configValue;
        if (description != null) {
            this.description = description;
        }
    }

    public String getConfigKey() { return configKey; }
    public String getConfigValue() { return configValue; }
    public String getDescription() { return description; }
    public int getVersion() { return version; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
