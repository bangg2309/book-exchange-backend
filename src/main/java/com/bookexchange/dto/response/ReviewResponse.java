package com.bookexchange.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponse {
    long id;
    String reviewer;
    String seller;
    String bookName;
    int rating;
    String comment;
    String createdAt;
}
