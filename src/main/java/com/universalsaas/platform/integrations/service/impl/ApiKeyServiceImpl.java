package com.universalsaas.platform.integrations.service.impl;

import com.universalsaas.platform.integrations.dto.ApiKeyCreateRequest;
import com.universalsaas.platform.integrations.dto.ApiKeyResponse;
import com.universalsaas.platform.integrations.dto.ApiKeyUsageLogResponse;
import com.universalsaas.platform.integrations.entity.ApiKey;
import com.universalsaas.platform.integrations.enums.ApiKeyStatus;
import com.universalsaas.platform.integrations.exception.IntegrationNotFoundException;
import com.universalsaas.platform.integrations.repository.ApiKeyRepository;
import com.universalsaas.platform.integrations.repository.ApiKeyUsageLogRepository;
import com.universalsaas.platform.integrations.service.ApiKeyService;
import com.universalsaas.platform.integrations.service.IntegrationLogService;
import com.universalsaas.platform.integrations.service.TenantContextService;
import com.universalsaas.platform.integrations.util.ApiKeyGenerator;
import com.universalsaas.platform.integrations.util.DateFormatUtil;
import com.universalsaas.platform.integrations.util.JsonUtil;
import com.universalsaas.platform.integrations.util.TokenMaskingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyUsageLogRepository usageLogRepository;
    private final TenantContextService tenantContextService;
    private final IntegrationLogService logService;

    @Override
    @Transactional
    public ApiKeyResponse create(ApiKeyCreateRequest request) {
        Long tenantId = tenantContextService.getCurrentTenantId();
        String plainKey = ApiKeyGenerator.generateApiKey();
        String plainSecret = ApiKeyGenerator.generateApiSecret();

        ApiKey apiKey = ApiKey.builder()
                .tenantId(tenantId)
                .keyName(request.getKeyName())
                .apiKeyHash(ApiKeyGenerator.hash(plainKey))
                .apiSecretHash(ApiKeyGenerator.hash(plainSecret))
                .maskedKey(TokenMaskingUtil.mask(plainKey))
                .permissions(request.getPermissions() != null ? String.join(",", request.getPermissions()) : "")
                .ipWhitelist(request.getIpWhitelist() != null ? String.join(",", request.getIpWhitelist()) : "")
                .expiryDate(request.getExpiryDate())
                .status(ApiKeyStatus.ACTIVE)
                .createdBy(tenantContextService.getCurrentUserId())
                .build();

        apiKey = apiKeyRepository.save(apiKey);
        logService.log(tenantId, null, "API_KEY", "api_key_created", "create",
                JsonUtil.toJson(request), "{\"id\":" + apiKey.getId() + "}", "SUCCESS", 200, null, 0);

        ApiKeyResponse response = toResponse(apiKey);
        response.setApiKey(plainKey);
        response.setApiSecret(plainSecret);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApiKeyResponse> list() {
        Long tenantId = tenantContextService.getCurrentTenantId();
        return apiKeyRepository.findByTenantId(tenantId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApiKeyResponse update(Long id, ApiKeyCreateRequest request) {
        ApiKey apiKey = findKey(id);
        apiKey.setKeyName(request.getKeyName());
        if (request.getPermissions() != null) {
            apiKey.setPermissions(String.join(",", request.getPermissions()));
        }
        if (request.getIpWhitelist() != null) {
            apiKey.setIpWhitelist(String.join(",", request.getIpWhitelist()));
        }
        apiKey.setExpiryDate(request.getExpiryDate());
        return toResponse(apiKeyRepository.save(apiKey));
    }

    @Override
    @Transactional
    public ApiKeyResponse regenerate(Long id) {
        ApiKey apiKey = findKey(id);
        String plainKey = ApiKeyGenerator.generateApiKey();
        String plainSecret = ApiKeyGenerator.generateApiSecret();
        apiKey.setApiKeyHash(ApiKeyGenerator.hash(plainKey));
        apiKey.setApiSecretHash(ApiKeyGenerator.hash(plainSecret));
        apiKey.setMaskedKey(TokenMaskingUtil.mask(plainKey));
        apiKey.setStatus(ApiKeyStatus.ACTIVE);
        apiKey.setRevokedAt(null);
        apiKey = apiKeyRepository.save(apiKey);

        ApiKeyResponse response = toResponse(apiKey);
        response.setApiKey(plainKey);
        response.setApiSecret(plainSecret);
        return response;
    }

    @Override
    @Transactional
    public void revoke(Long id) {
        ApiKey apiKey = findKey(id);
        apiKey.setStatus(ApiKeyStatus.REVOKED);
        apiKey.setRevokedAt(LocalDateTime.now());
        apiKeyRepository.save(apiKey);
        Long tenantId = tenantContextService.getCurrentTenantId();
        logService.log(tenantId, null, "API_KEY", "api_key_revoked", "revoke",
                "{\"id\":" + id + "}", null, "SUCCESS", 200, null, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApiKeyUsageLogResponse> getUsageLogs(Long id, Pageable pageable) {
        Long tenantId = tenantContextService.getCurrentTenantId();
        findKey(id);
        return usageLogRepository.findByTenantIdAndApiKeyIdOrderByCreatedAtDesc(tenantId, id, pageable)
                .map(log -> ApiKeyUsageLogResponse.builder()
                        .date(DateFormatUtil.formatDisplay(log.getCreatedAt()))
                        .endpoint(log.getEndpoint())
                        .method(log.getMethod())
                        .ipAddress(log.getIpAddress())
                        .status(log.getStatus())
                        .build());
    }

    private ApiKey findKey(Long id) {
        Long tenantId = tenantContextService.getCurrentTenantId();
        return apiKeyRepository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> new IntegrationNotFoundException("API key not found"));
    }

    private ApiKeyResponse toResponse(ApiKey apiKey) {
        List<String> permissions = apiKey.getPermissions() != null && !apiKey.getPermissions().isBlank()
                ? Arrays.asList(apiKey.getPermissions().split(",")) : List.of();
        List<String> ipWhitelist = apiKey.getIpWhitelist() != null && !apiKey.getIpWhitelist().isBlank()
                ? Arrays.asList(apiKey.getIpWhitelist().split(",")) : List.of();
        return ApiKeyResponse.builder()
                .id(apiKey.getId())
                .keyName(apiKey.getKeyName())
                .maskedKey(apiKey.getMaskedKey())
                .permissions(permissions)
                .ipWhitelist(ipWhitelist)
                .expiryDate(apiKey.getExpiryDate())
                .status(apiKey.getStatus() != null ? apiKey.getStatus().name() : null)
                .createdAt(apiKey.getCreatedAt())
                .build();
    }
}
