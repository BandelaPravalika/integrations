package com.universalsaas.platform.integrations.service;

import com.universalsaas.platform.integrations.dto.IntegrationLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IntegrationLogService {

    void log(Long tenantId, Long tenantIntegrationId, String integrationCode, String eventName,
             String action, String requestPayload, String responsePayload, String status,
             Integer httpStatus, String errorMessage, int retryCount);

    Page<IntegrationLogResponse> getLogs(String integrationCode, Pageable pageable);
}
