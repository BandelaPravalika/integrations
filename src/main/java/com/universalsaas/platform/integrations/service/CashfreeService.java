package com.universalsaas.platform.integrations.service;

import com.universalsaas.platform.integrations.dto.*;

import java.util.Map;

public interface CashfreeService {
    void configure(CashfreeConfigureRequest request);
    Map<String, Object> getStatus();
    Map<String, Object> createOrder(CashfreeCreateOrderRequest request);
    Map<String, Object> createPaymentLink(CashfreePaymentLinkRequest request);
    CashfreePaymentStatusResponse getPaymentStatus(String orderId);
    void handleWebhook(String payload);
    void disconnect();
}
