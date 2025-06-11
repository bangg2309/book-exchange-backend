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
public class OrderItemResponse {
    Long id;
    Long orderId;
    Long sellerId;
    String sellerName;
    BigDecimal shippingFee;
    BigDecimal totalAmount;
    String note;
    Integer status;
    List<OrderBookItemResponse> bookItems;
    LocalDateTime createdAt;
} 