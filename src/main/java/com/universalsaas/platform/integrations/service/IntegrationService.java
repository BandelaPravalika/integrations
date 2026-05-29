package com.universalsaas.platform.integrations.service;

import com.universalsaas.platform.integrations.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IntegrationService {

    List<IntegrationCardResponse> getAllIntegrations();

    IntegrationDetailsResponse getIntegrationDetails(String code);

    IntegrationDetailsResponse toggleIntegration(String code, IntegrationToggleRequest request);

    IntegrationDetailsResponse configureIntegration(String code, IntegrationConfigureRequest request);

    IntegrationTestResponse testIntegration(String code);

    void disconnectIntegration(String code);

    Page<IntegrationLogResponse> getIntegrationLogs(String code, Pageable pageable);

    List<SyncHistoryResponse> getSyncHistory(String code);

    OAuthConnectResponse getOAuthConnectUrl(String code);

    String handleOAuthCallback(String code, String authCode, String state);

    TenantIntegrationContext resolveContext(String code);
}
