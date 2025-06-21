package com.bookexchange.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListedBookDetailResponse {
    Long id;
    String title;
    Set<String> authors;
    Set<String> categories;
    Set<ReviewResponse> reviews;
    String isbn;
    String language;
    String publishYear;
    String conditionDescription;
    String pageCount;
    String sellerName;
    String address;
    BigDecimal priceNew;
    BigDecimal price;
    int conditionNumber;
    String schoolName;
    String publisher;
    String description;
    String thumbnail;
    Set<String> images;
    LocalDateTime createdAt;
    Integer status;
}
