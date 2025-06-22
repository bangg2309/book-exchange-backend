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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        Sort sortObj = Sort.by(Sort.Direction.fromString(direction), sort);
        Page<OrderResponse> orders = orderService.getAllOrders(PageRequest.of(page, size, sortObj), search);
        return ApiResponse.<Page<OrderResponse>>builder()
                .result(orders)
                .build();
    }

    @PatchMapping("/admin/{orderId}/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @PathVariable Integer status
    ) {
        OrderResponse order = orderService.updateOrderStatus(orderId, status);
        return ApiResponse.<OrderResponse>builder()
                .result(order)
                .build();
    }

    @DeleteMapping("/admin/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Boolean> deleteOrder(@PathVariable Long orderId) {
        boolean result = orderService.deleteOrder(orderId);
        return ApiResponse.<Boolean>builder()
                .result(result)
                .build();
    }
}
