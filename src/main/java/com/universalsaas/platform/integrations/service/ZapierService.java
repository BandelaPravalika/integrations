package com.universalsaas.platform.integrations.service;

import com.universalsaas.platform.integrations.dto.ZapierWebhookConfigureRequest;

import java.util.List;
import java.util.Map;

public interface ZapierService {
    Map<String, String> generateApiKey();
    Map<String, String> regenerateApiKey();
    void revokeApiKey();
    List<String> getTriggers();
    Map<String, Object> getSampleLead();
    void configureWebhook(ZapierWebhookConfigureRequest request);
    boolean sendEvent(String eventName, Object payload);
}
