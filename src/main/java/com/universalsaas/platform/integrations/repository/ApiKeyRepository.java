package com.universalsaas.platform.integrations.repository;

import com.universalsaas.platform.integrations.entity.ApiKey;
import com.universalsaas.platform.integrations.enums.ApiKeyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    List<ApiKey> findByTenantId(Long tenantId);

    Optional<ApiKey> findByTenantIdAndId(Long tenantId, Long id);

    Optional<ApiKey> findByApiKeyHashAndStatus(String apiKeyHash, ApiKeyStatus status);
}
