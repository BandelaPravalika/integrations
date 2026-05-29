package com.universalsaas.platform.integrations.service;

import com.universalsaas.platform.integrations.dto.GoogleCalendarEventRequest;

import java.util.Map;

public interface GoogleCalendarService {
    Map<String, Object> createEvent(GoogleCalendarEventRequest request);
}
