package com.universalsaas.platform.integrations.service;

import com.universalsaas.platform.integrations.dto.ApiKeyCreateRequest;
import com.universalsaas.platform.integrations.dto.ApiKeyResponse;
import com.universalsaas.platform.integrations.dto.ApiKeyUsageLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ApiKeyService {
    ApiKeyResponse create(ApiKeyCreateRequest request);
    List<ApiKeyResponse> list();
    ApiKeyResponse update(Long id, ApiKeyCreateRequest request);
    ApiKeyResponse regenerate(Long id);
    void revoke(Long id);
    Page<ApiKeyUsageLogResponse> getUsageLogs(Long id, Pageable pageable);
}
