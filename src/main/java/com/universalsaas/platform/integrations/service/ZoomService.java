package com.universalsaas.platform.integrations.service;

import com.universalsaas.platform.integrations.dto.IntegrationTestResponse;
import com.universalsaas.platform.integrations.dto.OAuthConnectResponse;
import com.universalsaas.platform.integrations.dto.ZoomMeetingRequest;

import java.util.Map;

public interface ZoomService {
    OAuthConnectResponse buildConnectUrl();
    String handleCallback(String authCode, String state);
    Map<String, Object> getStatus();
    Map<String, Object> createMeeting(ZoomMeetingRequest request);
    Map<String, Object> updateMeeting(String meetingId, ZoomMeetingRequest request);
    void deleteMeeting(String meetingId);
    IntegrationTestResponse testConnection();
    void disconnect();
}
