package com.bookexchange.service;

import com.bookexchange.dto.request.OrderBookItemRequest;
import com.bookexchange.dto.request.OrderCreationRequest;
import com.bookexchange.dto.request.OrderItemRequest;
import com.bookexchange.dto.response.OrderResponse;
import com.bookexchange.entity.*;
import com.bookexchange.exception.AppException;
import com.bookexchange.exception.ErrorCode;
import com.bookexchange.mapper.OrderMapper;
import com.bookexchange.repository.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    OrderBookItemRepository orderBookItemRepository;
    UserRepository userRepository;
    ListedBookRepository listedBookRepository;
    ShippingAddressRepository shippingAddressRepository;
    VoucherRepository voucherRepository;
    ShoppingCartRepository shoppingCartRepository;
    CartItemRepository cartItemRepository;
    CartService cartService;
    OrderMapper orderMapper;

    /**
     * Process a checkout request and create orders
     * @param request The order creation request
     * @return The created order response
     */
    @Transactional
    public OrderResponse checkout(OrderCreationRequest request) {
        // 1. Validate user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 2. Validate shipping address
        ShippingAddress shippingAddress = shippingAddressRepository.findById(request.getShippingAddressId())
                .orElseThrow(() -> new AppException(ErrorCode.SHIPPING_ADDRESS_NOT_FOUND));

        if (shippingAddress.getUser().getId() != (user.getId())) {
            throw new AppException(ErrorCode.SHIPPING_ADDRESS_NOT_BELONG_TO_USER);
        }

        // 3. Validate and apply voucher if provided
        Voucher voucher = null;
        if (request.getVoucherCode() != null && !request.getVoucherCode().isEmpty()) {
            voucher = voucherRepository.findByCode(request.getVoucherCode())
                    .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

            // Additional voucher validation could go here (expiration, usage limits, etc.)
        }

        // 4. Create the order
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPlatformVoucher(voucher);
        order.setShippingFee(request.getShippingFee());
        order.setDiscount(request.getDiscount());
        order.setTotalPrice(request.getTotalPrice());
        order.setStatus(1); // 1 = Pending

        // Save order to get the ID
        Order savedOrder = orderRepository.save(order);

        // 5. Process order items by seller
        List<OrderItem> orderItems = new ArrayList<>();

        // Group items by seller
        Map<Long, List<OrderItemRequest>> itemsBySeller = request.getItems().stream()
                .collect(Collectors.groupingBy(OrderItemRequest::getSellerId));

        for (Map.Entry<Long, List<OrderItemRequest>> entry : itemsBySeller.entrySet()) {
            Long sellerId = entry.getKey();
            List<OrderItemRequest> sellerItems = entry.getValue();

            // Validate seller
            User seller = userRepository.findById(sellerId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            // Create order item (representing all books from one seller)
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setSeller(seller);
            orderItem.setShippingFee(sellerItems.getFirst().getShippingFee());
            orderItem.setNote(sellerItems.getFirst().getNote());
            orderItem.setStatus(1); // 1 = Pending

            // Calculate total amount for this seller's items
            BigDecimal totalAmount = BigDecimal.ZERO;

            // Save the order item first to get its ID
            OrderItem savedOrderItem = orderItemRepository.save(orderItem);

            // Process each book item
            for (OrderItemRequest itemRequest : sellerItems) {
                for (OrderBookItemRequest bookItemRequest : itemRequest.getBookItems()) {
                    // Validate book
                    ListedBook book = listedBookRepository.findById(bookItemRequest.getBookId())
                            .orElseThrow(() -> new AppException(ErrorCode.LISTED_BOOK_NOT_FOUND));

                    // Validate book availability
                    if (book.getStatus() != 1) {
                        throw new AppException(ErrorCode.LISTED_BOOK_NOT_AVAILABLE);
                    }

                    // Create order book item
                    OrderBookItem orderBookItem = new OrderBookItem();
                    orderBookItem.setOrderItem(savedOrderItem);
                    orderBookItem.setBook(book);
                    orderBookItem.setQuantity(bookItemRequest.getQuantity());
                    orderBookItem.setPrice(bookItemRequest.getPrice());
                    orderBookItem.setSubtotal(bookItemRequest.getSubtotal());

                    // Save book item
                    orderBookItemRepository.save(orderBookItem);

                    // Add to total
                    totalAmount = totalAmount.add(bookItemRequest.getSubtotal());

                    // Mark book as sold/pending delivery
                    book.setStatus(2); // 2 = Sold/Pending delivery
                    listedBookRepository.save(book);
                }
            }

            // Update total amount
            savedOrderItem.setTotalAmount(totalAmount.add(savedOrderItem.getShippingFee()));
            orderItemRepository.save(savedOrderItem);

            orderItems.add(savedOrderItem);
        }

        // 6. Clear the user's cart
        cartService.clearCart(user.getId());

        // 7. Return the order response
        return orderMapper.toOrderResponse(savedOrder, orderItems);
    }

    /**
     * Get orders for a user
     * @param userId The user ID
     * @return List of orders
     */
    public List<OrderResponse> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(order -> {
                    List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
                    return orderMapper.toOrderResponse(order, items);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get orders for a seller
     * @param sellerId The seller ID
     * @return List of orders containing items sold by the seller
     */
    public List<OrderResponse> getOrdersBySeller(Long sellerId) {
        // Get all order items for this seller
        List<OrderItem> sellerItems = orderItemRepository.findBySellerId(sellerId);

        // Group by order
        Map<Long, List<OrderItem>> orderItemsMap = sellerItems.stream()
                .collect(Collectors.groupingBy(item -> item.getOrder().getId()));

        // Create responses
        return orderItemsMap.values().stream()
                .map(orderItems -> {
                    Order order = orderItems.getFirst().getOrder();
                    return orderMapper.toOrderResponse(order, orderItems);
                })
                .collect(Collectors.toList());
    }
}