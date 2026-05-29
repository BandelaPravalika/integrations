package com.universalsaas.platform.integrations.repository;

import com.universalsaas.platform.integrations.entity.IntegrationSyncHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntegrationSyncHistoryRepository extends JpaRepository<IntegrationSyncHistory, Long> {

    List<IntegrationSyncHistory> findByTenantIdAndTenantIntegrationIdOrderByStartedAtDesc(
            Long tenantId, Long tenantIntegrationId);

    List<IntegrationSyncHistory> findByTenantIntegrationIdOrderByStartedAtDesc(Long tenantIntegrationId);
}
