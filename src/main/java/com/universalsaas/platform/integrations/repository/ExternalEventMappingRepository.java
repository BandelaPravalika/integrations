package com.universalsaas.platform.integrations.repository;

import com.universalsaas.platform.integrations.entity.ExternalEventMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExternalEventMappingRepository extends JpaRepository<ExternalEventMapping, Long> {

    Optional<ExternalEventMapping> findByTenantIdAndProviderAndExternalEventId(
            Long tenantId, String provider, String externalEventId);
}
