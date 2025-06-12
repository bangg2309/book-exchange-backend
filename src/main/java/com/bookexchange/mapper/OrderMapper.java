package com.bookexchange.mapper;

import com.bookexchange.dto.response.OrderBookItemResponse;
import com.bookexchange.dto.response.OrderItemResponse;
import com.bookexchange.dto.response.OrderResponse;
import com.bookexchange.dto.response.ShippingAddressResponse;
import com.bookexchange.entity.Order;
import com.bookexchange.entity.OrderBookItem;
import com.bookexchange.entity.OrderItem;
import com.bookexchange.entity.ShippingAddress;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper  {
    
    public OrderResponse toOrderResponse(Order order, List<OrderItem> orderItems) {
        if (order == null) {
            return null;
        }
        
        OrderResponse.OrderResponseBuilder builder = OrderResponse.builder();
        
        builder.id(order.getId());
        builder.userId(order.getUser().getId());
        builder.shippingAddress(toShippingAddressResponse(order.getShippingAddress()));
        builder.paymentMethod(order.getPaymentMethod());
        builder.paymentTransactionId(order.getPaymentTransactionId());
        builder.shippingFee(order.getShippingFee());
        builder.discount(order.getDiscount());
        builder.totalPrice(order.getTotalPrice());
        builder.status(order.getStatus());
        builder.createdAt(order.getCreatedAt());
        builder.updatedAt(order.getUpdatedAt());
        
        if (orderItems != null) {
            builder.items(orderItems.stream()
                    .map(this::toOrderItemResponse)
                    .collect(Collectors.toList()));
        }
        
        return builder.build();
    }
    
    public OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        
        OrderItemResponse.OrderItemResponseBuilder builder = OrderItemResponse.builder();
        
        builder.id(orderItem.getId());
        if (orderItem.getOrder() != null) {
            builder.orderId(orderItem.getOrder().getId());
        }
        if (orderItem.getSeller() != null) {
            builder.sellerId(orderItem.getSeller().getId());
            builder.sellerName(orderItem.getSeller().getFullName());
        }
        builder.shippingFee(orderItem.getShippingFee());
        builder.totalAmount(orderItem.getTotalAmount());
        builder.note(orderItem.getNote());
        builder.status(orderItem.getStatus());
        builder.createdAt(orderItem.getCreatedAt());
        
        if (orderItem.getBookItems() != null) {
            builder.bookItems(orderItem.getBookItems().stream()
                    .map(this::toOrderBookItemResponse)
                    .collect(Collectors.toList()));
        }
        
        return builder.build();
    }
    
    public OrderBookItemResponse toOrderBookItemResponse(OrderBookItem orderBookItem) {
        if (orderBookItem == null) {
            return null;
        }
        
        OrderBookItemResponse.OrderBookItemResponseBuilder builder = OrderBookItemResponse.builder();
        
        builder.id(orderBookItem.getId());
        if (orderBookItem.getBook() != null) {
            builder.bookId(orderBookItem.getBook().getId());
            builder.bookTitle(orderBookItem.getBook().getTitle());
            builder.thumbnail(orderBookItem.getBook().getThumbnail());
            builder.condition(orderBookItem.getBook().getConditionNumber());
        }
        builder.quantity(orderBookItem.getQuantity());
        builder.price(orderBookItem.getPrice());
        builder.subtotal(orderBookItem.getSubtotal());
        
        return builder.build();
    }
    
    private ShippingAddressResponse toShippingAddressResponse(ShippingAddress shippingAddress) {
        if (shippingAddress == null) {
            return null;
        }
        
        return ShippingAddressResponse.builder()
                .id(shippingAddress.getId())
                .userId(shippingAddress.getUser().getId())
                .fullName(shippingAddress.getFullName())
                .phoneNumber(shippingAddress.getPhoneNumber())
                .province(shippingAddress.getProvince())
                .district(shippingAddress.getDistrict())
                .ward(shippingAddress.getWard())
                .addressDetail(shippingAddress.getAddressDetail())
                .build();
    }
} 