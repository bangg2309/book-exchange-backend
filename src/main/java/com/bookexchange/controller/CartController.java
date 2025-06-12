package com.bookexchange.controller;

import com.bookexchange.dto.request.ApiResponse;
import com.bookexchange.dto.request.CartAdditionRequest;
import com.bookexchange.dto.response.CartResponse;
import com.bookexchange.service.CartService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;


    @GetMapping("/{userId}")
    public ApiResponse<List<CartResponse>> getCart(@PathVariable long userId) {

        List<CartResponse> cartResponse = cartService.getCart(userId);
        return ApiResponse.<List<CartResponse>>builder()
                .result(cartResponse)
                .build();
    }

    @GetMapping("/count/{userId}")
    public ApiResponse<Long> getCartItemCount(@PathVariable long userId) {
        long itemCount = cartService.getCartItemCount(userId);
        return ApiResponse.<Long>builder()
                .result(itemCount)
                .build();
    }

    @PostMapping
    public ApiResponse<Void> addItemToCart(@Valid @RequestBody CartAdditionRequest request) {
        cartService.addItemToCart(request);
        return ApiResponse.<Void>builder()
                .build();
    }

    @DeleteMapping("/{userId}/{id}")
    public ApiResponse<Void> removeItemFromCart(@PathVariable long userId, @PathVariable long id) {
        cartService.removeItemFromCart(userId, id);
        return ApiResponse.<Void>builder()
                .build();
    }

    @DeleteMapping("/clear/{userId}")
    public ApiResponse<Void> clearCart(@PathVariable long userId) {
        cartService.clearCart(userId);
        return ApiResponse.<Void>builder()
                .build();
    }
}
