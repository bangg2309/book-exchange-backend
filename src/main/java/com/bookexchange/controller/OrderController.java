package com.bookexchange.controller;

import com.bookexchange.dto.request.ApiResponse;
import com.bookexchange.dto.request.OrderCreationRequest;
import com.bookexchange.dto.response.OrderResponse;
import com.bookexchange.service.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderController {
    OrderService orderService;

    /**
     * Process checkout and create a new order
     */
    @PostMapping("/checkout")
    public ApiResponse<OrderResponse> checkout(@Valid @RequestBody OrderCreationRequest request) {
        OrderResponse orderResponse = orderService.checkout(request);
        return ApiResponse.<OrderResponse>builder()
                .result(orderResponse)
                .build();
    }
    
    /**
     * Get orders for a user
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<OrderResponse>> getOrdersByUser(@PathVariable Long userId) {
        List<OrderResponse> orders = orderService.getOrdersByUser(userId);
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orders)
                .build();
    }
    
    /**
     * Get orders for a seller
     */
    @GetMapping("/seller/{sellerId}")
    public ApiResponse<List<OrderResponse>> getOrdersBySeller(@PathVariable Long sellerId) {
        List<OrderResponse> orders = orderService.getOrdersBySeller(sellerId);
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orders)
                .build();
    }
}
