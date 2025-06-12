package com.bookexchange.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    Long id;
    Long userId;
    ShippingAddressResponse shippingAddress;
    String paymentMethod;
    String paymentTransactionId;
    String deliveryMethod;
    String note;
    String voucherCode;
    BigDecimal shippingFee;
    BigDecimal discount;
    BigDecimal totalPrice;
    int status;
    List<OrderItemResponse> items;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
} 