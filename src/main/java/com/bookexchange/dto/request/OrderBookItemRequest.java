package com.bookexchange.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderBookItemRequest {
    @NotNull
    private Long bookId;
    
    @NotNull
    @Positive
    private Integer quantity;
    
    @NotNull
    private BigDecimal price;
    
    @NotNull
    private BigDecimal subtotal;
} 