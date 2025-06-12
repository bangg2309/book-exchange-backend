package com.bookexchange.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SlideRequest {
    String imageUrl;   // URL của hình ảnh
    String event;      // sự kiện liên quan
    int status;        // 1 = ACTIVE, 0 = INACTIVE
}
