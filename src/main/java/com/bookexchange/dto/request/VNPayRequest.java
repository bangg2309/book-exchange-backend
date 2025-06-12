package com.bookexchange.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VNPayRequest {
    private Long orderId;
    private String orderInfo;
    private BigDecimal amount;
    private String bankCode;
    private String language;
    private String returnUrl;
    private String ipAddress;
} 