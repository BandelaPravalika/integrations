package com.universalsaas.platform.integrations.repository;

import com.universalsaas.platform.integrations.entity.WebhookSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebhookSubscriptionRepository extends JpaRepository<WebhookSubscription, Long> {

    List<WebhookSubscription> findByTenantId(Long tenantId);

    List<WebhookSubscription> findByTenantIdAndEnabledTrue(Long tenantId);

    Optional<WebhookSubscription> findByTenantIdAndId(Long tenantId, Long id);
}
