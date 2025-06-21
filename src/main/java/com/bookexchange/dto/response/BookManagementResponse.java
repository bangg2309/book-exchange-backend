package com.bookexchange.dto.response;

import com.bookexchange.entity.ListedBook;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookManagementResponse {
    long id;
    String title;
    BigDecimal priceNew;
    BigDecimal price;
    int conditionNumber;
    String description;
    String thumbnail;
    String publisher;
    String school;
    String name;
    List<AuthorResponse> author;
    Integer status;
}
