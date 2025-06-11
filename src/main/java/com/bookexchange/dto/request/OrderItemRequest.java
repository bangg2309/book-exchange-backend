package com.bookexchange.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemRequest {
    @NotNull
    Long sellerId;
    
    BigDecimal shippingFee;
    
    String note;
    
    @NotEmpty
    List<OrderBookItemRequest> bookItems;
} 