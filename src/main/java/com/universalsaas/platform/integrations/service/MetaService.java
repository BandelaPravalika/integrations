package com.universalsaas.platform.integrations.service;

import com.universalsaas.platform.integrations.dto.MetaConfigureRequest;

import java.util.List;
import java.util.Map;

public interface MetaService {
    void configure(MetaConfigureRequest request);
    Map<String, Object> getStatus();
    List<Map<String, Object>> getPages();
    List<Map<String, Object>> getForms();
    Map<String, Object> syncLeads();
    void handleWebhook(String payload, String signature);
}
