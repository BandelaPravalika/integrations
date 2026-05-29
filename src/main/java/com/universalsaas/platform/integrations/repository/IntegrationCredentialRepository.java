package com.universalsaas.platform.integrations.repository;

import com.universalsaas.platform.integrations.entity.IntegrationCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IntegrationCredentialRepository extends JpaRepository<IntegrationCredential, Long> {

    Optional<IntegrationCredential> findByTenantIntegrationId(Long tenantIntegrationId);
}
