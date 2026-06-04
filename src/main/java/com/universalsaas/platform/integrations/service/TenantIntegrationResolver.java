package com.universalsaas.platform.integrations.service;

import com.universalsaas.platform.integrations.entity.IntegrationDefinition;
import com.universalsaas.platform.integrations.entity.TenantIntegration;
import com.universalsaas.platform.integrations.enums.IntegrationHealth;
import com.universalsaas.platform.integrations.enums.IntegrationStatus;
import com.universalsaas.platform.integrations.exception.IntegrationNotFoundException;
import com.universalsaas.platform.integrations.repository.IntegrationDefinitionRepository;
import com.universalsaas.platform.integrations.repository.TenantIntegrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resolves tenant + integration records without depending on IntegrationService
 * (avoids circular dependency with OAuth provider services).
 */
@Service
@RequiredArgsConstructor
public class TenantIntegrationResolver {

    private final IntegrationDefinitionRepository definitionRepository;
    private final TenantIntegrationRepository tenantIntegrationRepository;
    private final TenantContextService tenantContextService;

    /**
     * For listing integrations — does not insert rows (safe inside read-only transactions).
     */
    @Transactional(readOnly = true)
    public TenantIntegration findOrDefault(IntegrationDefinition def) {
        Long tenantId = tenantContextService.getCurrentTenantId();
        return tenantIntegrationRepository.findByTenantIdAndCode(tenantId, def.getCode())
                .orElseGet(() -> TenantIntegration.builder()
                        .tenantId(tenantId)
                        .integrationDefinitionId(def.getId())
                        .code(def.getCode())
                        .enabled(false)
                        .connected(false)
                        .status(IntegrationStatus.DISCONNECTED)
                        .health(IntegrationHealth.UNKNOWN)
                        .build());
    }

    @Transactional
    public TenantIntegrationContext resolveContext(String code) {
        Long tenantId = tenantContextService.getCurrentTenantId();
        return resolveContext(tenantId, code);
    }

    @Transactional
    public TenantIntegrationContext resolveContext(Long tenantId, String code) {
        String upper = code.toUpperCase();

        IntegrationDefinition def = definitionRepository.findByCodeIgnoreCase(upper)
                .orElseThrow(() -> new IntegrationNotFoundException(
                        "Integration definition not found: " + code));

        TenantIntegration ti = getOrCreateTenantIntegration(tenantId, def);

        return TenantIntegrationContext.builder()
                .tenantId(tenantId)
                .definition(def)
                .tenantIntegration(ti)
                .build();
    }

    private TenantIntegration getOrCreateTenantIntegration(Long tenantId, IntegrationDefinition def) {
        return tenantIntegrationRepository.findByTenantIdAndCode(tenantId, def.getCode())
                .orElseGet(() -> tenantIntegrationRepository.save(TenantIntegration.builder()
                        .tenantId(tenantId)
                        .integrationDefinitionId(def.getId())
                        .code(def.getCode())
                        .enabled(false)
                        .connected(false)
                        .status(IntegrationStatus.DISCONNECTED)
                        .health(IntegrationHealth.UNKNOWN)
                        .build()));
    }
}
