package com.bookexchange.service;

import com.bookexchange.dto.request.ShippingAddressRequest;
import com.bookexchange.dto.response.ShippingAddressResponse;
import com.bookexchange.entity.ShippingAddress;
import com.bookexchange.entity.User;
import com.bookexchange.exception.AppException;
import com.bookexchange.exception.ErrorCode;
import com.bookexchange.mapper.ShippingAddressMapper;
import com.bookexchange.repository.ShippingAddressRepository;
import com.bookexchange.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShippingAddressService {
    ShippingAddressRepository shippingAddressRepository;
    ShippingAddressMapper shippingAddressMapper;
    UserRepository userRepository;

    public ShippingAddressResponse create(ShippingAddressRequest request) {
        // Validate user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        ShippingAddress shippingAddress = shippingAddressMapper.toEntity(request);
        shippingAddress.setUser(user);
        
        // Save the address
        ShippingAddress savedAddress = shippingAddressRepository.save(shippingAddress);
        
        // Return the response
        return shippingAddressMapper.toResponse(savedAddress);
    }

    public List<ShippingAddressResponse> getByUserId(long userId) {
        return shippingAddressRepository.findByUserId(userId)
                .stream()
                .map(shippingAddressMapper::toResponse)
                .toList();
    }
}
