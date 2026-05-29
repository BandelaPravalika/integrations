package com.universalsaas.platform.integrations.service;

import com.universalsaas.platform.integrations.dto.GoogleDriveUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface GoogleDriveService {
    GoogleDriveUploadResponse upload(MultipartFile file, String module, Long referenceId);
}
