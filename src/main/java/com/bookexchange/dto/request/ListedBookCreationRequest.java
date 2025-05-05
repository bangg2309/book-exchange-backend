package com.bookexchange.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListedBookCreationRequest {

    long sellerId;
    String title;
    List<String> authors;
    Set<Long> categoriesId;
    String publisher;
    String publishYear;
    String isbn;
    String description;
    int conditionNumber;
    String conditionDescription;
    long schoolId;
    Set<String> imagesUrl;
    String address;
    BigDecimal priceNew;
    BigDecimal price;
    int pageCount;
    String language;
    String thumbnail;

}
