package com.universalsaas.platform.integrations.scheduler;

import com.universalsaas.platform.integrations.config.IntegrationProperties;
import com.universalsaas.platform.integrations.entity.WebhookDeliveryLog;
import com.universalsaas.platform.integrations.enums.WebhookDeliveryStatus;
import com.universalsaas.platform.integrations.repository.WebhookDeliveryLogRepository;
import com.universalsaas.platform.integrations.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookRetryScheduler {

    private final WebhookDeliveryLogRepository deliveryLogRepository;
    private final WebhookService webhookService;
    private final IntegrationProperties integrationProperties;

    @Scheduled(fixedRate = 300000)
    public void retryFailedDeliveries() {
        int maxRetry = integrationProperties.getWebhook().getMaxRetry();
        List<WebhookDeliveryLog> pending = deliveryLogRepository
                .findByStatusAndRetryCountLessThanAndNextRetryAtBefore(
                        WebhookDeliveryStatus.FAILED, maxRetry, LocalDateTime.now());

        for (WebhookDeliveryLog deliveryLog : pending) {
            try {
                webhookService.retry(deliveryLog.getWebhookSubscriptionId(), deliveryLog.getId());
            } catch (Exception e) {
                deliveryLog.setRetryCount(deliveryLog.getRetryCount() + 1);
                if (deliveryLog.getRetryCount() >= maxRetry) {
                    deliveryLog.setStatus(WebhookDeliveryStatus.FAILED);
                }
                deliveryLog.setNextRetryAt(LocalDateTime.now().plusMinutes(
                        integrationProperties.getWebhook().getRetryDelayMinutes()));
                deliveryLogRepository.save(deliveryLog);
            }
        }
    }
}
