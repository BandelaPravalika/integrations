package com.universalsaas.platform.integrations.repository;

import com.universalsaas.platform.integrations.entity.IntegrationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegrationLogRepository extends JpaRepository<IntegrationLog, Long> {

    Page<IntegrationLog> findByTenantIdAndIntegrationCodeOrderByCreatedAtDesc(
            Long tenantId, String integrationCode, Pageable pageable);

    Page<IntegrationLog> findByTenantIdOrderByCreatedAtDesc(Long tenantId, Pageable pageable);
}
