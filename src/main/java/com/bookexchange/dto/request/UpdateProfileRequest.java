package com.bookexchange.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProfileRequest {
    String fullName;
    String email;     // Email người dùng
    String phone;     // Số điện thoại người dùng
    String avatar;    // URL của ảnh đại diện
}
