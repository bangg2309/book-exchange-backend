package com.bookexchange.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherResponse {
    private Long id;
    private String code;
    private String description;
    private BigDecimal discountAmount;
    private Integer discountPercentage;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscount;
    private LocalDateTime startsAt;
    private LocalDateTime expiresAt;
    private Integer maxUses;
    private Integer currentUses;
    private Long sellerId;
    private String sellerName;
    private boolean isPlatformVoucher;
    private LocalDateTime createdAt;
} 