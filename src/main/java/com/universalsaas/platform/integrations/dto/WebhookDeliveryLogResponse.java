package com.universalsaas.platform.integrations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookDeliveryLogResponse {
    private Long id;
    private String eventName;
    private String status;
    private Integer httpStatus;
    private Integer retryCount;
    private String date;
}
