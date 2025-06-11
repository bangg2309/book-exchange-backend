package com.bookexchange.dto.response;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShippingAddressResponse {
    Long id;
    Long userId;
    String fullName;
    String phoneNumber;
    String province;
    String district;
    String ward;
    String addressDetail;
    boolean defaultAddress;
}
