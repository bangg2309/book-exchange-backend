package com.bookexchange.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SlideResponse {
    long id;
    String imageUrl;
    String addedBy;           // username hoặc tên người dùng
    LocalDateTime addedAt;    // thời điểm thêm
    String event;             // sự kiện liên quan
    int status;               // 1 = ACTIVE, 0 = INACTIVE
}
