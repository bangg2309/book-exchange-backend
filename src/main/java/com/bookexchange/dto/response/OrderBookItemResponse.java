package com.bookexchange.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderBookItemResponse {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String thumbnail;
    private Integer condition;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
} 