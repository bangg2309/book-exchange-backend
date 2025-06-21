package com.bookexchange.controller;

import com.bookexchange.dto.request.ApiResponse;
import com.bookexchange.dto.response.OrderItemResponse;
import com.bookexchange.service.OrderItemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-items")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderItemController {
    OrderItemService orderItemService;

    @GetMapping("/user/{userId}")
    public ApiResponse<List<OrderItemResponse>> getOrderItemsByUserId(@PathVariable Long userId) {
        List<OrderItemResponse> orderItems = orderItemService.getOrderItemsByUserId(userId);
        return ApiResponse.<List<OrderItemResponse>>builder()
                .result(orderItems)
                .build();
    }

    @GetMapping("/seller/{sellerId}")
    public ApiResponse<List<OrderItemResponse>> getOrderItemsBySellerId(@PathVariable Long sellerId) {
        List<OrderItemResponse> orderItems = orderItemService.getOrderItemsBySellerId(sellerId);
        return ApiResponse.<List<OrderItemResponse>>builder()
                .result(orderItems)
                .build();
    }

    @GetMapping("/{orderItemId}")
    public ApiResponse<OrderItemResponse> getOrderItemById(@PathVariable Long orderItemId) {
        OrderItemResponse orderItem = orderItemService.getOrderItemById(orderItemId);
        return ApiResponse.<OrderItemResponse>builder()
                .result(orderItem)
                .build();
    }

    @PatchMapping("/{orderItemId}/status/{status}")
    public ApiResponse<OrderItemResponse> updateOrderItemStatus(
            @PathVariable Long orderItemId,
            @PathVariable Integer status
    ) {
        OrderItemResponse orderItem = orderItemService.updateOrderItemStatus(orderItemId, status);
        return ApiResponse.<OrderItemResponse>builder()
                .result(orderItem)
                .build();
    }
} 