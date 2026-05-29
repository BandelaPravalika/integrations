package com.universalsaas.platform.integrations.scheduler;

import com.universalsaas.platform.integrations.repository.TenantIntegrationRepository;
import com.universalsaas.platform.integrations.service.MetaService;
import com.universalsaas.platform.integrations.service.TenantContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetaLeadSyncScheduler {

    private final TenantIntegrationRepository tenantIntegrationRepository;
    private final MetaService metaService;
    private final TenantContextService tenantContextService;

    @Scheduled(cron = "0 */30 * * * *")
    public void syncMetaLeads() {
        Long tenantId = tenantContextService.getCurrentTenantId();
        tenantIntegrationRepository.findByTenantIdAndCode(tenantId, "META")
                .filter(ti -> Boolean.TRUE.equals(ti.getEnabled()) && Boolean.TRUE.equals(ti.getConnected()))
                .ifPresent(ti -> {
                    try {
                        metaService.syncLeads();
                    } catch (Exception e) {
                        log.warn("Scheduled Meta lead sync failed: {}", e.getMessage());
                    }
                });
    }
}
