package com.bookexchange.dto.response;

import com.bookexchange.entity.ListedBook;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryManagementResponse {
    long id;
    String name;
    String description;
    String imageUrl;
    Set<ListedBook> books = new HashSet<>();
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
