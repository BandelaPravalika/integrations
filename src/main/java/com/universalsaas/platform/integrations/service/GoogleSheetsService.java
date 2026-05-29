package com.universalsaas.platform.integrations.service;

import com.universalsaas.platform.integrations.dto.GoogleSheetsExportRequest;

import java.util.Map;

public interface GoogleSheetsService {
    Map<String, Object> export(GoogleSheetsExportRequest request);
}
