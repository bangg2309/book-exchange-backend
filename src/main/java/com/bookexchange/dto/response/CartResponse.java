package com.bookexchange.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
    long id;
    long bookId;
    String bookTitle;
    String thumbnail;
    String description;
    BigDecimal priceNew;
    BigDecimal price;
    int quantity;
    String sellerName;
    int conditionNumber;
}
