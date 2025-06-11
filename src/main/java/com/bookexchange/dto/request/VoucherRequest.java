package com.bookexchange.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherRequest {
    @NotBlank
    private String code;

    private String description;

    private BigDecimal discountAmount;

    private Integer discountPercentage;

    @PositiveOrZero
    private BigDecimal minOrderValue;

    @PositiveOrZero
    private BigDecimal maxDiscount;

    @NotNull
    private LocalDateTime startsAt;

    @NotNull
    private LocalDateTime expiresAt;

    @Positive
    private Integer maxUses;

    private Long sellerId; // Null for platform vouchers
} 