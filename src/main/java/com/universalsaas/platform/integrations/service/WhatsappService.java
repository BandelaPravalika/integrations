package com.universalsaas.platform.integrations.service;

import com.universalsaas.platform.integrations.dto.IntegrationTestResponse;
import com.universalsaas.platform.integrations.dto.WhatsappConfigureRequest;
import com.universalsaas.platform.integrations.dto.WhatsappSendRequest;

import java.util.Map;

public interface WhatsappService {
    void configure(WhatsappConfigureRequest request);
    Map<String, Object> getStatus();
    void sendMessage(WhatsappSendRequest request);
    IntegrationTestResponse testConnection();
    void disconnect();
}
