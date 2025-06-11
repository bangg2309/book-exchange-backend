package com.bookexchange.mapper;

import com.bookexchange.dto.request.ShippingAddressRequest;
import com.bookexchange.dto.response.ShippingAddressResponse;
import com.bookexchange.entity.ShippingAddress;
import org.springframework.stereotype.Component;

@Component
public class ShippingAddressMapper {

    public ShippingAddressResponse toResponse(ShippingAddress shippingAddress) {
        return ShippingAddressResponse.builder()
                .id(shippingAddress.getId())
                .userId(shippingAddress.getUser().getId())
                .fullName(shippingAddress.getFullName())
                .phoneNumber(shippingAddress.getPhoneNumber())
                .district(shippingAddress.getDistrict())
                .province(shippingAddress.getProvince())
                .ward(shippingAddress.getWard())
                .addressDetail(shippingAddress.getAddressDetail())
                .defaultAddress(shippingAddress.isDefaultAddress())
                .build();
    }

    public ShippingAddress toEntity(ShippingAddressRequest request) {
        ShippingAddress shippingAddress = new ShippingAddress();

        shippingAddress.setFullName(request.getFullName());
        shippingAddress.setPhoneNumber(request.getPhoneNumber());
        shippingAddress.setProvince(request.getProvince());
        shippingAddress.setDistrict(request.getDistrict());
        shippingAddress.setWard(request.getWard());
        shippingAddress.setAddressDetail(request.getAddressDetail());
        shippingAddress.setDefaultAddress(request.isDefaultAddress());
        return shippingAddress;
    }
}
