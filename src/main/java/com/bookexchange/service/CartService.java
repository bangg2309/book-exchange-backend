package com.bookexchange.service;

import com.bookexchange.dto.request.CartAdditionRequest;
import com.bookexchange.dto.response.CartResponse;
import com.bookexchange.entity.CartItem;
import com.bookexchange.entity.ListedBook;
import com.bookexchange.entity.ShoppingCart;
import com.bookexchange.exception.AppException;
import com.bookexchange.exception.ErrorCode;
import com.bookexchange.mapper.CartMapper;
import com.bookexchange.repository.CartItemRepository;
import com.bookexchange.repository.ListedBookRepository;
import com.bookexchange.repository.ShoppingCartRepository;
import com.bookexchange.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {
    ShoppingCartRepository shoppingCartRepository;
    CartItemRepository cartItemRepository;
    UserRepository userRepository;
    ListedBookRepository listedBookRepository;
    CartMapper cartMapper;

    public List<CartResponse> getCart(long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        return shoppingCart
                .getItems()
                .stream()
                .map(cartMapper::toCartResponse).
                toList();
    }


    public void addItemToCart(CartAdditionRequest request) {
        // 1. Find and create a shopping cart for the user
        ShoppingCart cart = shoppingCartRepository.findByUserId(request.getUserId())
                .orElseGet(() -> createCartForUser(request.getUserId()));

        // 2. Find the listed book by ID
        ListedBook listedBook = listedBookRepository.findById(request.getBookId())
                .orElseThrow(() -> new AppException(ErrorCode.LISTED_BOOK_NOT_FOUND));
        if (listedBook.getStatus() != 1) {
            throw new AppException(ErrorCode.LISTED_BOOK_NOT_AVAILABLE);
        }
        // 3. Check if the item already exists in the cart
        if (cart.getItems().stream().anyMatch(item -> item.getListedBook().getId().equals(request.getBookId()))) {
            throw new AppException(ErrorCode.ITEM_ALREADY_IN_CART);
        }

        // 4. Create a new cart item and add it to the cart
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setListedBook(listedBook);
        cartItemRepository.save(cartItem);
    }

    public void removeItemFromCart(Long userId, Long itemId) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));
        if (!(cartItem.getCart().getUser().getId() == userId)) {
            throw new AppException(ErrorCode.CART_ITEM_NOT_BELONG_TO_USER);
        }
        cartItemRepository.delete(cartItem);
    }

    public long getCartItemCount(long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        return shoppingCart.getItems().size();
    }

    @Transactional
    public void clearCart(long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        shoppingCart.getItems().clear();
        shoppingCartRepository.save(shoppingCart);
    }

    private ShoppingCart createCartForUser(Long userId) {
        ShoppingCart cart = new ShoppingCart();
        cart.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
        return shoppingCartRepository.save(cart);
    }


}
