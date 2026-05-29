package com.universalsaas.platform.integrations.service;

import java.util.Map;
import java.util.Optional;

public interface IntegrationSettingService {

    void saveSetting(Long tenantIntegrationId, String key, String value, boolean encrypted);

    void saveSettings(Long tenantIntegrationId, Map<String, String> settings);

    Optional<String> getSetting(Long tenantIntegrationId, String key);

    String getSettingOrDefault(Long tenantIntegrationId, String key, String defaultValue);

    Map<String, String> getAllSettings(Long tenantIntegrationId);
}
