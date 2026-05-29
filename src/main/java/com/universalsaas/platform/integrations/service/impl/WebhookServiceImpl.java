package com.universalsaas.platform.integrations.service.impl;

import com.universalsaas.platform.integrations.config.IntegrationProperties;
import com.universalsaas.platform.integrations.dto.WebhookDeliveryLogResponse;
import com.universalsaas.platform.integrations.dto.WebhookSubscriptionRequest;
import com.universalsaas.platform.integrations.dto.WebhookSubscriptionResponse;
import com.universalsaas.platform.integrations.entity.WebhookDeliveryLog;
import com.universalsaas.platform.integrations.entity.WebhookSubscription;
import com.universalsaas.platform.integrations.enums.WebhookDeliveryStatus;
import com.universalsaas.platform.integrations.exception.IntegrationNotFoundException;
import com.universalsaas.platform.integrations.repository.WebhookDeliveryLogRepository;
import com.universalsaas.platform.integrations.repository.WebhookSubscriptionRepository;
import com.universalsaas.platform.integrations.service.EncryptionService;
import com.universalsaas.platform.integrations.service.TenantContextService;
import com.universalsaas.platform.integrations.service.WebhookService;
import com.universalsaas.platform.integrations.util.ApiKeyGenerator;
import com.universalsaas.platform.integrations.util.DateFormatUtil;
import com.universalsaas.platform.integrations.util.HmacUtil;
import com.universalsaas.platform.integrations.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

    private final WebhookSubscriptionRepository subscriptionRepository;
    private final WebhookDeliveryLogRepository deliveryLogRepository;
    private final TenantContextService tenantContextService;
    private final EncryptionService encryptionService;
    private final IntegrationProperties integrationProperties;
    private final RestTemplate restTemplate;

    @Override
    @Transactional
    public WebhookSubscriptionResponse create(WebhookSubscriptionRequest request) {
        Long tenantId = tenantContextService.getCurrentTenantId();
        String secret = ApiKeyGenerator.generateApiSecret();
        WebhookSubscription sub = WebhookSubscription.builder()
                .tenantId(tenantId)
                .name(request.getName())
                .webhookUrl(request.getWebhookUrl())
                .secretKeyEncrypted(encryptionService.encrypt(secret))
                .events(request.getEvents() != null ? String.join(",", request.getEvents()) : "")
                .enabled(request.getEnabled() != null ? request.getEnabled() : true)
                .build();
        return toResponse(subscriptionRepository.save(sub));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebhookSubscriptionResponse> list() {
        Long tenantId = tenantContextService.getCurrentTenantId();
        return subscriptionRepository.findByTenantId(tenantId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WebhookSubscriptionResponse update(Long id, WebhookSubscriptionRequest request) {
        WebhookSubscription sub = findSubscription(id);
        sub.setName(request.getName());
        sub.setWebhookUrl(request.getWebhookUrl());
        if (request.getEvents() != null) {
            sub.setEvents(String.join(",", request.getEvents()));
        }
        if (request.getEnabled() != null) {
            sub.setEnabled(request.getEnabled());
        }
        return toResponse(subscriptionRepository.save(sub));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        WebhookSubscription sub = findSubscription(id);
        subscriptionRepository.delete(sub);
    }

    @Override
    @Transactional
    public void test(Long id) {
        deliverToSubscription(findSubscription(id), "test.event", Map.of("message", "Test webhook delivery"), "TEST", null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WebhookDeliveryLogResponse> getLogs(Long id, Pageable pageable) {
        Long tenantId = tenantContextService.getCurrentTenantId();
        return deliveryLogRepository.findByTenantIdAndWebhookSubscriptionIdOrderByCreatedAtDesc(tenantId, id, pageable)
                .map(log -> WebhookDeliveryLogResponse.builder()
                        .id(log.getId())
                        .eventName(log.getEventName())
                        .status(log.getStatus() != null ? log.getStatus().name() : null)
                        .httpStatus(log.getHttpStatus())
                        .retryCount(log.getRetryCount())
                        .date(DateFormatUtil.formatDisplay(log.getCreatedAt()))
                        .build());
    }

    @Override
    @Transactional
    public void retry(Long subscriptionId, Long logId) {
        Long tenantId = tenantContextService.getCurrentTenantId();
        WebhookDeliveryLog log = deliveryLogRepository.findByTenantIdAndId(tenantId, logId)
                .orElseThrow(() -> new IntegrationNotFoundException("Delivery log not found"));
        WebhookSubscription sub = findSubscription(subscriptionId);
        try {
            Map<String, Object> payload = JsonUtil.fromJson(log.getPayload(), Map.class);
            deliverToSubscription(sub, log.getEventName(), payload, "RETRY", null);
        } catch (Exception e) {
            log.setStatus(WebhookDeliveryStatus.FAILED);
            log.setRetryCount(log.getRetryCount() + 1);
            deliveryLogRepository.save(log);
        }
    }

    @Override
    @Transactional
    public void deliverEvent(String eventName, Map<String, Object> payload, String module, Long referenceId) {
        Long tenantId = tenantContextService.getCurrentTenantId();
        List<WebhookSubscription> subs = subscriptionRepository.findByTenantIdAndEnabledTrue(tenantId);
        Map<String, Object> body = buildPayload(eventName, tenantId, module, referenceId, payload);
        for (WebhookSubscription sub : subs) {
            if (sub.getEvents() == null || sub.getEvents().isBlank() || sub.getEvents().contains(eventName)) {
                deliverToSubscription(sub, eventName, body, module, referenceId);
            }
        }
    }

    private void deliverToSubscription(WebhookSubscription sub, String eventName, Map<String, Object> body,
                                     String module, Long referenceId) {
        Long tenantId = tenantContextService.getCurrentTenantId();
        String payloadJson = JsonUtil.toJson(body);
        String secret = encryptionService.decrypt(sub.getSecretKeyEncrypted());
        String signature = HmacUtil.sign(payloadJson, secret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Webhook-Signature", signature);
        headers.set("X-Webhook-Event", eventName);
        headers.set("X-Tenant-Id", String.valueOf(tenantId));

        WebhookDeliveryLog log = WebhookDeliveryLog.builder()
                .tenantId(tenantId)
                .webhookSubscriptionId(sub.getId())
                .eventName(eventName)
                .payload(payloadJson)
                .retryCount(0)
                .status(WebhookDeliveryStatus.PENDING)
                .build();

        try {
            ResponseEntity<String> response = restTemplate.exchange(sub.getWebhookUrl(), HttpMethod.POST,
                    new HttpEntity<>(body, headers), String.class);
            log.setResponse(response.getBody());
            log.setHttpStatus(response.getStatusCode().value());
            log.setStatus(response.getStatusCode().is2xxSuccessful()
                    ? WebhookDeliveryStatus.SUCCESS : WebhookDeliveryStatus.FAILED);
        } catch (Exception e) {
            log.setStatus(WebhookDeliveryStatus.FAILED);
            log.setResponse(e.getMessage());
            log.setNextRetryAt(LocalDateTime.now().plusMinutes(integrationProperties.getWebhook().getRetryDelayMinutes()));
        }
        deliveryLogRepository.save(log);
    }

    private Map<String, Object> buildPayload(String eventName, Long tenantId, String module,
                                             Long referenceId, Map<String, Object> data) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("event", eventName);
        body.put("tenantId", tenantId);
        body.put("module", module);
        body.put("referenceId", referenceId);
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("data", data != null ? data : Map.of());
        return body;
    }

    private WebhookSubscription findSubscription(Long id) {
        Long tenantId = tenantContextService.getCurrentTenantId();
        return subscriptionRepository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> new IntegrationNotFoundException("Webhook subscription not found"));
    }

    private WebhookSubscriptionResponse toResponse(WebhookSubscription sub) {
        List<String> events = sub.getEvents() != null && !sub.getEvents().isBlank()
                ? Arrays.asList(sub.getEvents().split(",")) : List.of();
        return WebhookSubscriptionResponse.builder()
                .id(sub.getId())
                .name(sub.getName())
                .webhookUrl(sub.getWebhookUrl())
                .events(events)
                .enabled(Boolean.TRUE.equals(sub.getEnabled()))
                .build();
    }
}
