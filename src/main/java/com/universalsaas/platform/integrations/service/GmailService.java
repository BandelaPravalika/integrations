package com.universalsaas.platform.integrations.service;

import com.universalsaas.platform.integrations.dto.GmailSendRequest;

public interface GmailService {
    void sendEmail(GmailSendRequest request);
}
