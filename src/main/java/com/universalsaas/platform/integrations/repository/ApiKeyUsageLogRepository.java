package com.universalsaas.platform.integrations.repository;

import com.universalsaas.platform.integrations.entity.ApiKeyUsageLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiKeyUsageLogRepository extends JpaRepository<ApiKeyUsageLog, Long> {

    Page<ApiKeyUsageLog> findByTenantIdAndApiKeyIdOrderByCreatedAtDesc(
            Long tenantId, Long apiKeyId, Pageable pageable);
}
