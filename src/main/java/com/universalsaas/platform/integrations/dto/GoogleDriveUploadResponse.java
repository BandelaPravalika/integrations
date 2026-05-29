package com.universalsaas.platform.integrations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleDriveUploadResponse {
    private String fileId;
    private String fileUrl;
    private String fileName;
}
