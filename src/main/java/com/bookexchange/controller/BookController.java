package com.bookexchange.controller;

import com.bookexchange.dto.request.ApiResponse;
import com.bookexchange.dto.request.RejectBookRequest;
import com.bookexchange.dto.response.BookManagementResponse;
import com.bookexchange.dto.response.ListedBookDetailResponse;
import com.bookexchange.service.ListedBookService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BookController {

    ListedBookService listedBookService;

    /**
     * Lấy danh sách sách theo trạng thái với phân trang
     * @param status Trạng thái sách (0: chưa duyệt, 1: đã duyệt, 2: từ chối)
     * @param page Trang hiện tại
     * @param size Số lượng mỗi trang
     * @param sortBy Trường để sắp xếp (mặc định: createdAt)
     * @param sortDir Hướng sắp xếp (ASC hoặc DESC)
     * @return Danh sách sách với phân trang và sắp xếp
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<BookManagementResponse>> getBooksByStatus(
            @PathVariable Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir
    ) {
        log.info("Getting books with status: {}, sort by: {}, direction: {}", status, sortBy, sortDir);
        Sort sort = Sort.Direction.fromString(sortDir.toUpperCase()).equals(Sort.Direction.ASC) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return ApiResponse.<Page<BookManagementResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(listedBookService.getBooksByStatus(status, PageRequest.of(page, size, sort)))
                .build();
    }

    /**
     * Lấy chi tiết sách (dùng lại method từ ListedBookController)
     * @param id ID của sách
     * @return Chi tiết sách
     */
    @GetMapping("/{id}/detail")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ListedBookDetailResponse> getBookDetail(@PathVariable Long id) {
        return ApiResponse.<ListedBookDetailResponse>builder()
                .code(HttpStatus.OK.value())
                .result(listedBookService.getListedDetail(id))
                .build();
    }

    /**
     * API phê duyệt sách - chỉ admin mới có quyền
     * @param id ID của sách cần phê duyệt
     * @return ApiResponse chứa thông tin kết quả
     */
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> approveBook(@PathVariable Long id) {
        log.info("Request to approve book with ID: {}", id);
        boolean result = listedBookService.approveBook(id);
        
        if (result) {
            return ApiResponse.<String>builder()
                    .code(HttpStatus.OK.value())
                    .message("Book approved successfully")
                    .result("Success")
                    .build();
        } else {
            return ApiResponse.<String>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("Book could not be approved. It may already be approved or in another status.")
                    .result("Failed")
                    .build();
        }
    }
    
    /**
     * API từ chối sách - chỉ admin mới có quyền
     * @param id ID của sách cần từ chối
     * @param request Thông tin từ chối
     * @return ApiResponse chứa thông tin kết quả
     */
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> rejectBook(
            @PathVariable Long id,
            @RequestBody(required = false) RejectBookRequest request
    ) {
        String reason = request != null ? request.getReason() : null;
        log.info("Request to reject book with ID: {}, reason: {}", id, reason);
        
        // Sửa lại: Không sử dụng setRejectionReason hiện tại
        boolean result = listedBookService.rejectBook(id, reason);
        
        if (result) {
            return ApiResponse.<String>builder()
                    .code(HttpStatus.OK.value())
                    .message("Book rejected successfully")
                    .result("Success")
                    .build();
        } else {
            return ApiResponse.<String>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("Book could not be rejected. It may already be processed or in another status.")
                    .result("Failed")
                    .build();
        }
    }
} 