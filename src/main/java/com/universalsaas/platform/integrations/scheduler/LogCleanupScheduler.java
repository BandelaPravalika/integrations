package com.universalsaas.platform.integrations.scheduler;

import com.universalsaas.platform.integrations.config.IntegrationProperties;
import com.universalsaas.platform.integrations.repository.IntegrationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogCleanupScheduler {

    private final IntegrationLogRepository logRepository;
    private final IntegrationProperties integrationProperties;

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanupOldLogs() {
        int retentionDays = integrationProperties.getLogs().getRetentionDays();
        LocalDateTime cutoff = LocalDateTime.now().minusDays(retentionDays);
        logRepository.findAll().stream()
                .filter(l -> l.getCreatedAt() != null && l.getCreatedAt().isBefore(cutoff))
                .forEach(logRepository::delete);
        log.info("Integration logs older than {} days cleaned up", retentionDays);
    }
}
