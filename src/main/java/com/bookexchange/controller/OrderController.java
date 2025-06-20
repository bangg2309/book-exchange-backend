package com.bookexchange.controller;

import com.bookexchange.dto.request.ApiResponse;
import com.bookexchange.dto.request.OrderCreationRequest;
import com.bookexchange.dto.response.OrderResponse;
import com.bookexchange.dto.response.OrderItemResponse;
import com.bookexchange.service.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
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

    /**
     * Lấy đơn bán của người dùng hiện tại
     */
    @GetMapping("/seller/me")
    public ApiResponse<List<OrderResponse>> getCurrentUserSellOrders() {
        List<OrderResponse> orders = orderService.getCurrentUserSellOrders();
        
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orders)
                .build();
    }

    /**
     * Lấy thông tin đơn hàng theo ID
     * 
     * @param orderId ID của đơn hàng
     * @return thông tin đơn hàng
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        OrderResponse order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }
}
