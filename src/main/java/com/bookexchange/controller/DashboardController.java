package com.bookexchange.controller;

import com.bookexchange.dto.request.ApiResponse;
import com.bookexchange.dto.response.ListedBooksResponse;
import com.bookexchange.dto.response.RevenueStatsDTO;
import com.bookexchange.service.ListedBookService;
import com.bookexchange.service.OrderService;
import com.bookexchange.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DashboardController {
    
    OrderService orderService;
    UserService userService;
    ListedBookService listedBookService;
    
    @GetMapping("/stats/orders/total")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Long> getTotalOrders() {
        return ApiResponse.<Long>builder()
                .result(orderService.countTotalOrders())
                .build();
    }
    
    @GetMapping("/stats/books/total")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Long> getTotalBooks() {
        return ApiResponse.<Long>builder()
                .result(listedBookService.countTotalBooks())
                .build();
    }
    
    @GetMapping("/stats/categories/total")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Long> getTotalCategories() {
        return ApiResponse.<Long>builder()
                .result(listedBookService.countTotalCategories())
                .build();
    }
    
    @GetMapping("/stats/users/total")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Long> getTotalUsers() {
        return ApiResponse.<Long>builder()
                .result(userService.countTotalUsers())
                .build();
    }
    
    /**
     * Lấy 3 cuốn sách mới nhất đã được phê duyệt cho trang dashboard
     */
    @GetMapping("/recent-books")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<ListedBooksResponse>> getRecentBooks() {
        return ApiResponse.<List<ListedBooksResponse>>builder()
                .result(listedBookService.getRecentApprovedBooks(3))
                .build();
    }
    
    @GetMapping("/stats/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<RevenueStatsDTO> getRevenueStats(
            @RequestParam(defaultValue = "day") String period
    ) {
        RevenueStatsDTO revenueData = orderService.getRevenueStats(period);
        return ApiResponse.<RevenueStatsDTO>builder()
                .result(revenueData)
                .build();
    }
} 