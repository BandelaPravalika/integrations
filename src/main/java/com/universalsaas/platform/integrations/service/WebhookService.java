package com.universalsaas.platform.integrations.service;

import com.universalsaas.platform.integrations.dto.WebhookDeliveryLogResponse;
import com.universalsaas.platform.integrations.dto.WebhookSubscriptionRequest;
import com.universalsaas.platform.integrations.dto.WebhookSubscriptionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface WebhookService {
    WebhookSubscriptionResponse create(WebhookSubscriptionRequest request);
    java.util.List<WebhookSubscriptionResponse> list();
    WebhookSubscriptionResponse update(Long id, WebhookSubscriptionRequest request);
    void delete(Long id);
    void test(Long id);
    Page<WebhookDeliveryLogResponse> getLogs(Long id, Pageable pageable);
    void retry(Long subscriptionId, Long logId);
    void deliverEvent(String eventName, Map<String, Object> payload, String module, Long referenceId);
}
