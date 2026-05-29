package com.universalsaas.platform.integrations.service;

import com.universalsaas.platform.integrations.dto.IntegrationTestResponse;
import com.universalsaas.platform.integrations.dto.OAuthConnectResponse;

public interface GoogleOAuthService {
    OAuthConnectResponse buildConnectUrl();
    String handleCallback(String authCode, String state);
    IntegrationTestResponse testConnection();
    String getValidAccessToken();
}
