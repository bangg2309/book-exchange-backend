package com.bookexchange.mapper;

import com.bookexchange.dto.response.OrderBookItemResponse;
import com.bookexchange.dto.response.OrderItemResponse;
import com.bookexchange.entity.OrderBookItem;
import com.bookexchange.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderItemMapper {
    
    /**
     * Map a list of OrderItem entities to OrderItemResponse DTOs
     */
    public List<OrderItemResponse> toOrderItemResponses(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::toOrderItemResponse)
                .collect(Collectors.toList());
    }

    /**
     * Map a single OrderItem entity to OrderItemResponse DTO
     */
    public OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        
        // Map book items
        List<OrderBookItemResponse> bookItemResponses = orderItem.getBookItems().stream()
                .map(this::toOrderBookItemResponse)
                .collect(Collectors.toList());
        
        // Build order item response
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrder().getId())
                .sellerId(orderItem.getSeller().getId())
                .sellerName(orderItem.getSeller().getFullName()) // Using fullName instead of username
                .shippingFee(orderItem.getShippingFee())
                .totalAmount(orderItem.getTotalAmount())
                .note(orderItem.getNote())
                .status(orderItem.getStatus())
                .bookItems(bookItemResponses)
                .createdAt(orderItem.getCreatedAt())
                .updatedAt(orderItem.getUpdatedAt()) // Adding updatedAt field
                .build();
    }
    
    /**
     * Map OrderBookItem to its response DTO
     */
    public OrderBookItemResponse toOrderBookItemResponse(OrderBookItem bookItem) {
        if (bookItem == null) {
            return null;
        }
        
        return OrderBookItemResponse.builder()
                .id(bookItem.getId())
                .bookId(bookItem.getBook().getId())
                .bookTitle(bookItem.getBook().getTitle())
                .thumbnail(bookItem.getBook().getThumbnail())
                .condition(bookItem.getBook().getStatus()) // Using status as condition
                .quantity(bookItem.getQuantity())
                .price(bookItem.getPrice())
                .subtotal(bookItem.getSubtotal())
                .build();
    }
} 