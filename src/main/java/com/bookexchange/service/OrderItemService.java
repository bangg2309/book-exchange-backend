package com.bookexchange.service;

import com.bookexchange.constant.PredefineOrderItem;
import com.bookexchange.dto.response.OrderBookItemResponse;
import com.bookexchange.dto.response.OrderItemResponse;
import com.bookexchange.entity.OrderItem;
import com.bookexchange.entity.OrderBookItem;
import com.bookexchange.entity.User;
import com.bookexchange.exception.AppException;
import com.bookexchange.exception.ErrorCode;
import com.bookexchange.mapper.OrderItemMapper;
import com.bookexchange.repository.OrderItemRepository;
import com.bookexchange.repository.OrderBookItemRepository;
import com.bookexchange.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderItemService {
    OrderItemRepository orderItemRepository;
    OrderBookItemRepository orderBookItemRepository;
    UserRepository userRepository;
    OrderItemMapper orderItemMapper;

    public List<OrderItemResponse> getOrderItemsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<OrderItem> orderItems = orderItemRepository.findAll().stream()
                .filter(item -> Objects.equals(item.getOrder().getUser().getId(), userId))
                .collect(Collectors.toList());

        return orderItemMapper.toOrderItemResponses(orderItems);
    }

    /**
     * Get order items by seller ID
     */
    public List<OrderItemResponse> getOrderItemsBySellerId(Long sellerId) {
        // Verify seller exists
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        // Find all order items for this seller
        List<OrderItem> orderItems = orderItemRepository.findBySellerId(sellerId);
        
        // Map to response DTOs using the mapper
        return orderItemMapper.toOrderItemResponses(orderItems);
    }

    /**
     * Get order item by ID
     */
    public OrderItemResponse getOrderItemById(Long orderItemId) {
        // Find order item
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_ITEM_NOT_FOUND));
        
        // Map to response DTO using the mapper
        return orderItemMapper.toOrderItemResponse(orderItem);
    }

    /**
     * Update order item status
     */
    public OrderItemResponse updateOrderItemStatus(Long orderItemId, Integer status) {
        // Validate status code is valid
        validateOrderItemStatus(status);
        
        // Find order item
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_ITEM_NOT_FOUND));
        
        // Update status
        orderItem.setStatus(status);
        OrderItem updatedOrderItem = orderItemRepository.save(orderItem);
        
        // Map to response DTO using the mapper
        return orderItemMapper.toOrderItemResponse(updatedOrderItem);
    }

    /**
     * Validate that the status code is valid
     */
    private void validateOrderItemStatus(Integer status) {
        List<Integer> validStatuses = List.of(
                PredefineOrderItem.ORDER_ITEM_PENDING,
                PredefineOrderItem.ORDER_ITEM_WAITING_FOR_CONFIRMATION,
                PredefineOrderItem.ORDER_ITEM_CONFIRMED,
                PredefineOrderItem.ORDER_ITEM_WAITING_FOR_DELIVERY,
                PredefineOrderItem.ORDER_ITEM_RECEIVED,
                PredefineOrderItem.ORDER_ITEM_CANCELLED
        );
        
        if (!validStatuses.contains(status)) {
            throw new AppException(ErrorCode.INVALID_ORDER_ITEM_STATUS);
        }
    }
} 