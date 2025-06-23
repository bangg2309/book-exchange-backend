package com.bookexchange.service;

import com.bookexchange.dto.request.OrderBookItemRequest;
import com.bookexchange.dto.request.OrderCreationRequest;
import com.bookexchange.dto.request.OrderItemRequest;
import com.bookexchange.dto.response.OrderResponse;
import com.bookexchange.dto.response.RevenueStatsDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    // Số chữ số thập phân khi làm tròn
    private static final int DECIMAL_SCALE = 2;

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
    VoucherService voucherService;

    /**
     * Process a checkout request and create orders
     *
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

        // 3. Nhóm items theo người bán và tính subtotal
        Map<Long, List<OrderItemRequest>> itemsBySeller = request.getItems().stream()
                .collect(Collectors.groupingBy(OrderItemRequest::getSellerId));

        // Tính tổng giá trị hàng hóa và tổng phí vận chuyển
        BigDecimal subtotal = calculateSubtotal(request.getItems());
        BigDecimal totalShippingFee = BigDecimal.ZERO;

        for (List<OrderItemRequest> sellerItems : itemsBySeller.values()) {
            if (!sellerItems.isEmpty()) {
                totalShippingFee = totalShippingFee.add(sellerItems.getFirst().getShippingFee());
            }
        }

        // Tính tổng giá trị đơn hàng trước khi chiết khấu
        BigDecimal totalBeforeDiscount = subtotal.add(totalShippingFee);

        // 4. Validate và áp dụng voucher nếu có
        Voucher voucher = null;
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal discountRate = BigDecimal.ZERO;

        if (request.getVoucherCode() != null && !request.getVoucherCode().isEmpty()) {
            try {
                // Tính số tiền giảm giá
                discount = voucherService.validateAndCalculateDiscount(request.getVoucherCode(), subtotal);

                // Lấy thông tin voucher
                voucher = voucherRepository.findByCode(request.getVoucherCode())
                        .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

                // Cập nhật request với số tiền giảm giá
                request.setDiscount(discount);

                // Tính tỷ lệ giảm giá (dùng để phân bổ)
                if (subtotal.compareTo(BigDecimal.ZERO) > 0) {
                    discountRate = discount.divide(subtotal, DECIMAL_SCALE, RoundingMode.HALF_UP);
                }

                // Cập nhật tổng giá trị đơn hàng sau khi giảm giá
                BigDecimal newTotal = subtotal.add(totalShippingFee).subtract(discount);
                request.setTotalPrice(newTotal);

                // Đánh dấu voucher đã được sử dụng
                voucherService.applyVoucher(request.getVoucherCode());
            } catch (AppException e) {
                // Nếu voucher không hợp lệ, tiếp tục mà không áp dụng
                request.setVoucherCode(null);
            }
        } else {
            // Không có voucher, giá trị đơn hàng không thay đổi
            request.setTotalPrice(totalBeforeDiscount);
        }

        // 5. Tạo đơn hàng
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPlatformVoucher(voucher);
        order.setShippingFee(totalShippingFee);
        order.setDiscount(discount);
        order.setTotalPrice(request.getTotalPrice());
        order.setStatus(1); // 1 = Pending

        // Lưu order để lấy ID
        Order savedOrder = orderRepository.save(order);

        // 6. Xử lý các OrderItem (nhóm theo người bán)
        List<OrderItem> orderItems = new ArrayList<>();

        // Phân bổ số tiền giảm giá cho từng OrderItem
        BigDecimal totalDiscountApplied = BigDecimal.ZERO;
        BigDecimal remainingDiscount = discount;
        List<OrderItemWithSubtotal> orderItemsWithSubtotals = new ArrayList<>();

        for (Map.Entry<Long, List<OrderItemRequest>> entry : itemsBySeller.entrySet()) {
            Long sellerId = entry.getKey();
            List<OrderItemRequest> sellerItems = entry.getValue();

            // Xác thực người bán
            User seller = userRepository.findById(sellerId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            // Tạo OrderItem (đại diện cho tất cả sách từ một người bán)
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setSeller(seller);
            orderItem.setShippingFee(sellerItems.getFirst().getShippingFee());
            orderItem.setNote(sellerItems.getFirst().getNote());
            orderItem.setStatus(1); // 1 = Pending

            // Tính tổng giá trị đơn hàng từ người bán này
            BigDecimal sellerSubtotal = BigDecimal.ZERO;

            // Lưu OrderItem trước để lấy ID
            OrderItem savedOrderItem = orderItemRepository.save(orderItem);

            // Xử lý từng sách
            for (OrderItemRequest itemRequest : sellerItems) {
                for (OrderBookItemRequest bookItemRequest : itemRequest.getBookItems()) {
                    // Xác thực sách
                    ListedBook book = listedBookRepository.findById(bookItemRequest.getBookId())
                            .orElseThrow(() -> new AppException(ErrorCode.LISTED_BOOK_NOT_FOUND));

                    // Kiểm tra sách còn khả dụng không
                    if (book.getStatus() != 1) {
                        throw new AppException(ErrorCode.LISTED_BOOK_NOT_AVAILABLE);
                    }

                    // Tạo OrderBookItem
                    OrderBookItem orderBookItem = new OrderBookItem();
                    orderBookItem.setOrderItem(savedOrderItem);
                    orderBookItem.setBook(book);
                    orderBookItem.setQuantity(bookItemRequest.getQuantity());
                    orderBookItem.setPrice(bookItemRequest.getPrice());
                    orderBookItem.setSubtotal(bookItemRequest.getSubtotal());

                    // Lưu OrderBookItem
                    orderBookItemRepository.save(orderBookItem);

                    // Cộng vào tổng giá trị của người bán này
                    sellerSubtotal = sellerSubtotal.add(bookItemRequest.getSubtotal());

                    // Đánh dấu sách đã bán
                    book.setStatus(2); // 2 = Sold/Pending delivery
                    listedBookRepository.save(book);
                }
            }

            // Lưu lại thông tin để phân bổ giảm giá
            orderItemsWithSubtotals.add(new OrderItemWithSubtotal(savedOrderItem, sellerSubtotal));

            // Thêm vào danh sách kết quả
            orderItems.add(savedOrderItem);
        }

        // 7. Phân bổ giảm giá cho từng OrderItem
        int lastIndex = orderItemsWithSubtotals.size() - 1;

        for (int i = 0; i < orderItemsWithSubtotals.size(); i++) {
            OrderItemWithSubtotal item = orderItemsWithSubtotals.get(i);
            BigDecimal itemDiscount;

            if (i == lastIndex) {
                // Đơn hàng cuối cùng nhận phần giảm giá còn lại để tránh sai số làm tròn
                itemDiscount = remainingDiscount;
            } else {
                // Phân bổ theo tỷ lệ giá trị đơn hàng
                itemDiscount = item.subtotal.multiply(discountRate).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);
                remainingDiscount = remainingDiscount.subtract(itemDiscount);
            }

            // Cập nhật tổng tiền sau khi giảm giá
            BigDecimal totalWithShipping = item.orderItem.getShippingFee().add(item.subtotal);
            BigDecimal finalTotal = totalWithShipping.subtract(itemDiscount);

            // Lưu vào OrderItem
            item.orderItem.setTotalAmount(finalTotal);
            orderItemRepository.save(item.orderItem);

            // Cộng vào tổng giảm giá đã áp dụng
            totalDiscountApplied = totalDiscountApplied.add(itemDiscount);
        }

        // 8. Xóa giỏ hàng
        shoppingCartRepository.findByUserIdWithItems(user.getId()).ifPresent(shoppingCart -> cartService.clearCart(user.getId()));


        // 9. Trả về response
        return orderMapper.toOrderResponse(savedOrder, orderItems);
    }

    /**
     * Lớp phụ trợ để lưu OrderItem và giá trị subtotal của nó
     */
    private static class OrderItemWithSubtotal {
        final OrderItem orderItem;
        final BigDecimal subtotal;

        OrderItemWithSubtotal(OrderItem orderItem, BigDecimal subtotal) {
            this.orderItem = orderItem;
            this.subtotal = subtotal;
        }
    }

    /**
     * Calculate subtotal of all items in an order
     *
     * @param items List of order items
     * @return Total order value
     */
    private BigDecimal calculateSubtotal(List<OrderItemRequest> items) {
        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderItemRequest item : items) {
            for (OrderBookItemRequest bookItem : item.getBookItems()) {
                subtotal = subtotal.add(bookItem.getSubtotal());
            }
        }

        return subtotal;
    }

    /**
     * Get orders for a user
     *
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
     *
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

    /**
     * Cập nhật trạng thái thanh toán của đơn hàng
     *
     * @param orderId       mã đơn hàng
     * @param status        trạng thái mới
     * @param transactionId mã giao dịch từ cổng thanh toán
     * @return đơn hàng sau khi cập nhật
     */
    @Transactional
    public OrderResponse updateOrderPaymentStatus(Long orderId, int status, String transactionId) {
        // Tìm đơn hàng
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Cập nhật trạng thái và mã giao dịch
        order.setStatus(status);
        order.setPaymentTransactionId(transactionId);

        List<OrderItem> orderItems = new ArrayList<>(order.getOrderItems());

        // Cập nhật trạng thái các OrderItem
        for (OrderItem item : orderItems) {
            item.setStatus(status);
            orderItemRepository.save(item);
        }

        // Lưu đơn hàng
        Order savedOrder = orderRepository.save(order);

        // Chuyển đổi thành OrderResponse
        return orderMapper.toOrderResponse(savedOrder, orderItems);
    }

    /**
     * Lấy thông tin đơn hàng theo ID
     *
     * @param orderId ID đơn hàng
     * @return thông tin đơn hàng
     */
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        List<OrderItem> orderItems = new ArrayList<>(order.getOrderItems());
        return orderMapper.toOrderResponse(order, orderItems);
    }

    /**
     * Lấy danh sách đơn bán của người dùng hiện tại
     *
     * @return Danh sách đơn hàng mà người dùng hiện tại là người bán
     */
    public List<OrderResponse> getCurrentUserSellOrders() {
        // Lấy thông tin người dùng hiện tại từ SecurityContext
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Lấy danh sách đơn hàng của người bán
        List<OrderItem> sellerItems = orderItemRepository.findBySellerId(currentUser.getId());

        // Nhóm theo đơn hàng
        Map<Long, List<OrderItem>> orderItemsMap = sellerItems.stream()
                .collect(Collectors.groupingBy(item -> item.getOrder().getId()));

        // Tạo responses
        List<OrderResponse> responses = orderItemsMap.values().stream()
                .map(orderItems -> {
                    Order order = orderItems.get(0).getOrder();
                    return orderMapper.toOrderResponse(order, orderItems);
                })
                .collect(Collectors.toList());

        return responses;
    }

    /**
     * Admin: Get all orders with pagination and optional search
     *
     * @param pageable Pagination information
     * @param search   Optional search term
     * @return Page of order responses
     */
    public Page<OrderResponse> getAllOrders(Pageable pageable, String search) {
        Page<Order> ordersPage;

        if (search != null && !search.trim().isEmpty()) {
            // Tìm kiếm đơn giản theo ID
            try {
                Long orderId = Long.parseLong(search);
                ordersPage = orderRepository.findById(orderId)
                        .map(List::of)
                        .map(orders -> new PageImpl<>(orders, pageable, 1))
                        .orElse(new PageImpl<>(List.of(), pageable, 0));
            } catch (NumberFormatException e) {
                // Nếu không phải ID, tìm tất cả (không hỗ trợ tìm theo username)
                ordersPage = orderRepository.findAll(pageable);
            }
        } else {
            // Lấy tất cả đơn hàng
            ordersPage = orderRepository.findAll(pageable);
        }

        // Chuyển đổi thành OrderResponse
        List<OrderResponse> orderResponses = ordersPage.getContent().stream()
                .map(order -> {
                    List<OrderItem> items = new ArrayList<>(order.getOrderItems());
                    return orderMapper.toOrderResponse(order, items);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(orderResponses, pageable, ordersPage.getTotalElements());
    }

    /**
     * Admin: Update order status
     *
     * @param orderId Order ID
     * @param status  New status
     * @return Updated order response
     */
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, Integer status) {
        // Tìm đơn hàng
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Cập nhật trạng thái đơn hàng
        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);

        // Cập nhật trạng thái các OrderItem
        List<OrderItem> orderItems = new ArrayList<>(order.getOrderItems());
        for (OrderItem item : orderItems) {
            item.setStatus(status);
            orderItemRepository.save(item);
        }

        // Trả về OrderResponse
        return orderMapper.toOrderResponse(savedOrder, orderItems);
    }

    /**
     * Admin: Delete order
     *
     * @param orderId Order ID
     * @return true if successful
     */
    @Transactional
    public boolean deleteOrder(Long orderId) {
        // Tìm đơn hàng
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        try {
            // Xóa các OrderBookItem trước
            List<OrderItem> orderItems = new ArrayList<>(order.getOrderItems());
            for (OrderItem item : orderItems) {
                // Lấy và xóa các OrderBookItem thuộc về OrderItem này
                orderBookItemRepository.deleteAll(item.getBookItems());
            }

            // Xóa các OrderItem
            orderItemRepository.deleteAll(orderItems);

            // Xóa Order
            orderRepository.delete(order);

            return true;
        } catch (Exception e) {
            log.error("Error deleting order: {}", e.getMessage());
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public long countTotalOrders() {
        return orderRepository.count();
    }

    /**
     * Lấy dữ liệu doanh thu theo khoảng thời gian
     *
     * @param period Khoảng thời gian (day, week, month, year)
     * @return Object chứa dữ liệu doanh thu
     */
    public RevenueStatsDTO getRevenueStats(String period) {
        LocalDateTime startDate;
        LocalDateTime endDate = LocalDateTime.now();

        // Xác định khoảng thời gian dựa trên period
        switch (period.toLowerCase()) {
            case "day":
                // Lấy 30 ngày gần nhất
                startDate = endDate.minusDays(30);
                break;
            case "week":
                // Lấy 12 tuần gần nhất
                startDate = endDate.minusWeeks(12);
                break;
            case "month":
                // Lấy 12 tháng gần nhất
                startDate = endDate.minusMonths(12);
                break;
            case "year":
                // Lấy 5 năm gần nhất
                startDate = endDate.minusYears(5);
                break;
            default:
                // Mặc định lấy 12 tháng
                startDate = endDate.minusMonths(12);
                period = "month";
        }

        log.info("Fetching revenue stats for period: {}, from: {} to: {}", period, startDate, endDate);

        // Lấy tất cả đơn hàng đã xử lý hoặc hoàn thành (status >= 2)
        // Trạng thái đơn hàng: 1=PENDING, 2=PROCESSING, 3=SHIPPED, 4=DELIVERED, 5=CANCELLED, 6=REFUNDED
        int minOrderStatus = 2; // PROCESSING: đã xử lý/đã thanh toán

        // Tổng hợp dữ liệu doanh thu theo khoảng thời gian
        List<Object[]> revenueData;

        switch (period.toLowerCase()) {
            case "day":
                revenueData = orderRepository.getRevenueByDay(startDate, endDate, minOrderStatus);
                break;
            case "week":
                revenueData = orderRepository.getRevenueByWeek(startDate, endDate, minOrderStatus);
                break;
            case "month":
                revenueData = orderRepository.getRevenueByMonth(startDate, endDate, minOrderStatus);
                break;
            case "year":
                revenueData = orderRepository.getRevenueByYear(startDate, endDate, minOrderStatus);
                break;
            default:
                revenueData = orderRepository.getRevenueByMonth(startDate, endDate, minOrderStatus);
        }

        log.info("Revenue data size: {}", revenueData.size());

        // Chuyển đổi dữ liệu sang định dạng phù hợp
        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();

        for (Object[] row : revenueData) {
            String label = String.valueOf(row[0]); // Chuyển đổi an toàn thành String
            BigDecimal revenue;

            try {
                // Xử lý revenue một cách an toàn
                if (row[1] instanceof BigDecimal) {
                    revenue = (BigDecimal) row[1];
                } else if (row[1] instanceof Number) {
                    revenue = new BigDecimal(((Number) row[1]).doubleValue());
                } else {
                    revenue = new BigDecimal(String.valueOf(row[1]));
                }

                log.debug("Raw data: {} - {}", label, revenue);

                // Chuyển đổi nhãn thời gian cho người dùng
                switch (period.toLowerCase()) {
                    case "month":
                        // Chuyển từ "2023-5" thành "Tháng 5, 2023"
                        String[] parts = label.split("-");
                        if (parts.length == 2) {
                            label = "Tháng " + parts[1] + ", " + parts[0];
                        }
                        break;
                    case "week":
                        // Chuyển từ "2023-22" thành "Tuần 22, 2023"
                        parts = label.split("-");
                        if (parts.length == 2) {
                            label = "Tuần " + parts[1] + ", " + parts[0];
                        }
                        break;
                }

                labels.add(label);
                // Chuyển BigDecimal sang nghìn đồng (kVND)
                data.add(revenue.divide(new BigDecimal(1000), RoundingMode.HALF_UP).doubleValue());
            } catch (Exception e) {
                log.error("Error processing revenue data: {} - {}", row[0], row[1], e);
            }
        }

        log.info("Processed revenue data: labels={}, data={}", labels, data);

        // Tạo đối tượng kết quả
        return RevenueStatsDTO.builder()
                .labels(labels)
                .data(data)
                .period(period)
                .build();
    }
}