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
public class OrderCreationRequest {
    @NotNull
    Long userId;
    
    @NotNull
    Long shippingAddressId;
    
    String paymentMethod;
    
    String voucherCode;
    
    BigDecimal shippingFee;
    
    BigDecimal discount;
    
    @NotNull
    BigDecimal totalPrice;
    
    @NotEmpty
    List<OrderItemRequest> items;
}
