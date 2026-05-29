package com.universalsaas.platform.integrations.service;

import com.universalsaas.platform.integrations.entity.IntegrationDefinition;
import com.universalsaas.platform.integrations.entity.TenantIntegration;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TenantIntegrationContext {
    private Long tenantId;
    private IntegrationDefinition definition;
    private TenantIntegration tenantIntegration;
}
