package com.universalsaas.platform.integrations.service.impl;

import com.universalsaas.platform.integrations.dto.IntegrationLogResponse;
import com.universalsaas.platform.integrations.entity.IntegrationLog;
import com.universalsaas.platform.integrations.repository.IntegrationLogRepository;
import com.universalsaas.platform.integrations.service.IntegrationLogService;
import com.universalsaas.platform.integrations.service.TenantContextService;
import com.universalsaas.platform.integrations.util.DateFormatUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IntegrationLogServiceImpl implements IntegrationLogService {

    private final IntegrationLogRepository logRepository;
    private final TenantContextService tenantContextService;

    @Override
    @Transactional
    public void log(Long tenantId, Long tenantIntegrationId, String integrationCode, String eventName,
                    String action, String requestPayload, String responsePayload, String status,
                    Integer httpStatus, String errorMessage, int retryCount) {
        logRepository.save(IntegrationLog.builder()
                .tenantId(tenantId)
                .tenantIntegrationId(tenantIntegrationId)
                .integrationCode(integrationCode)
                .eventName(eventName)
                .action(action)
                .requestPayload(requestPayload)
                .responsePayload(responsePayload)
                .status(status)
                .httpStatus(httpStatus)
                .errorMessage(errorMessage)
                .retryCount(retryCount)
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IntegrationLogResponse> getLogs(String integrationCode, Pageable pageable) {
        Long tenantId = tenantContextService.getCurrentTenantId();
        return logRepository.findByTenantIdAndIntegrationCodeOrderByCreatedAtDesc(tenantId, integrationCode, pageable)
                .map(log -> IntegrationLogResponse.builder()
                        .date(DateFormatUtil.formatDisplay(log.getCreatedAt()))
                        .integration(log.getIntegrationCode())
                        .event(log.getEventName())
                        .action(log.getAction())
                        .status(log.getStatus())
                        .httpStatus(log.getHttpStatus())
                        .errorMessage(log.getErrorMessage())
                        .retryCount(log.getRetryCount())
                        .build());
    }
}
