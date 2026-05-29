package com.universalsaas.platform.integrations.service;

import com.universalsaas.platform.integrations.dto.SyncHistoryResponse;

import java.util.List;

public interface IntegrationSyncHistoryService {

    void record(Long tenantId, Long tenantIntegrationId, String syncType, String status,
                String message, int processed, int success, int failed);

    List<SyncHistoryResponse> getSyncHistory(String integrationCode);
}
