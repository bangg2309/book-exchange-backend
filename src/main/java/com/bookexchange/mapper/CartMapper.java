package com.bookexchange.mapper;

import com.bookexchange.dto.response.CartResponse;
import com.bookexchange.entity.CartItem;
import org.springframework.stereotype.Component;

@Component
public class CartMapper {
    public CartResponse toCartResponse(CartItem cartItem) {
        return CartResponse.builder()
                .id(cartItem.getId())
                .bookId(cartItem.getListedBook().getId())
                .quantity(1)
                .thumbnail(cartItem.getListedBook().getThumbnail())
                .sellerName(cartItem.getListedBook().getSeller().getFullName())
                .bookTitle(cartItem.getListedBook().getTitle())
                .description(cartItem.getListedBook().getDescription())
                .priceNew(cartItem.getListedBook().getPriceNew())
                .price(cartItem.getListedBook().getPrice())
                .conditionNumber(cartItem.getListedBook().getConditionNumber())
                .build();
    }
}
