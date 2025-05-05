package com.bookexchange.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListedBooksResponse {
    long id;
    String title;
    BigDecimal priceNew;
    BigDecimal price;
    int conditionNumber;
    String fullName;
    String schoolName;
    String author;
    String publisher;
    String description;
    String thumbnail;
}
