package com.bookexchange.controller;

import com.bookexchange.dto.request.ApiResponse;
import com.bookexchange.dto.request.ShippingAddressRequest;
import com.bookexchange.dto.response.ShippingAddressResponse;
import com.bookexchange.service.ShippingAddressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipping-addresses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShippingAddressController {
    ShippingAddressService shippingAddressService;

    @GetMapping("/{userId}")
    public ApiResponse<List<ShippingAddressResponse>> getByUserId(@PathVariable long userId) {
        return ApiResponse.<List<ShippingAddressResponse>>builder()
                .result(shippingAddressService.getByUserId(userId))
                .build();
    }

    @PostMapping
    public ApiResponse<ShippingAddressResponse> create(@RequestBody ShippingAddressRequest request) {
        ShippingAddressResponse response = shippingAddressService.create(request);
        return ApiResponse.<ShippingAddressResponse>builder()
                .result(response)
                .build();
    }
}
