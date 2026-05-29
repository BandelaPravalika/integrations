package com.universalsaas.platform.integrations.service;

import org.springframework.stereotype.Service;

/**
 * Provides current tenant context for multi-tenant operations.
 * TEMPORARY SUPER ADMIN MODE: returns default tenant 1.
 * TODO: Replace with JWT/session tenant resolution when auth is enabled.
 */
@Service
public class TenantContextService {

    public Long getCurrentTenantId() {
        // TODO: Read tenantId from JWT claims or SecurityContext when login is enabled
        return 1L;
    }

    public Long getCurrentUserId() {
        // TODO: Read userId from JWT when login is enabled
        return null;
    }
}
