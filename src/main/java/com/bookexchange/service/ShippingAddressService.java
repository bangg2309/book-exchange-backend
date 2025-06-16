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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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

        if (request.isDefaultAddress()) {
            // If this is a default address, set all other addresses for the user to non-default
            List<ShippingAddress> existingAddresses = shippingAddressRepository.findByUserId(user.getId());
            existingAddresses.forEach(address -> address.setDefaultAddress(false));
            shippingAddressRepository.saveAll(existingAddresses);
        }

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
    
    @Transactional
    public ShippingAddressResponse update(long id, ShippingAddressRequest request) {
        // Find the address to update
        ShippingAddress address = shippingAddressRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHIPPING_ADDRESS_NOT_FOUND));
        
        // Validate that the address belongs to the user in the request
        if (!Objects.equals(address.getUser().getId(), request.getUserId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        
        // Update the address fields
        address.setFullName(request.getFullName());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setProvince(request.getProvince());
        address.setDistrict(request.getDistrict());
        address.setWard(request.getWard());
        address.setAddressDetail(request.getAddressDetail());
        
        // Handle default address setting
        if (request.isDefaultAddress() && !address.isDefaultAddress()) {
            // If setting as default, update other addresses
            List<ShippingAddress> existingAddresses = shippingAddressRepository.findByUserId(request.getUserId());
            existingAddresses.stream()
                    .filter(a -> !Objects.equals(a.getId(), id))
                    .forEach(a -> a.setDefaultAddress(false));
            shippingAddressRepository.saveAll(existingAddresses);
            address.setDefaultAddress(true);
        } else {
            address.setDefaultAddress(request.isDefaultAddress());
        }
        
        // Save the updated address
        ShippingAddress updatedAddress = shippingAddressRepository.save(address);
        
        // Return the response
        return shippingAddressMapper.toResponse(updatedAddress);
    }
    
    @Transactional
    public boolean delete(long id) {
        // Find the address to delete
        ShippingAddress address = shippingAddressRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHIPPING_ADDRESS_NOT_FOUND));
        
        // Delete the address
        shippingAddressRepository.delete(address);
        
        return true;
    }
    
    @Transactional
    public ShippingAddressResponse setDefault(long id) {
        // Find the address to set as default
        ShippingAddress address = shippingAddressRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHIPPING_ADDRESS_NOT_FOUND));
        
        // If already default, just return it
        if (address.isDefaultAddress()) {
            return shippingAddressMapper.toResponse(address);
        }
        
        // Set all other addresses for this user to non-default
        List<ShippingAddress> existingAddresses = shippingAddressRepository.findByUserId(address.getUser().getId());
        existingAddresses.forEach(a -> a.setDefaultAddress(false));
        shippingAddressRepository.saveAll(existingAddresses);
        
        // Set this address as default
        address.setDefaultAddress(true);
        ShippingAddress updatedAddress = shippingAddressRepository.save(address);
        
        // Return the response
        return shippingAddressMapper.toResponse(updatedAddress);
    }
}
