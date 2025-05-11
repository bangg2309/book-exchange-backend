package com.bookexchange.controller;


import com.bookexchange.dto.request.ApiResponse;
import com.bookexchange.dto.response.BookDetailResponse;
import com.bookexchange.service.BookDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Nguyen Toan
 * @version BookController.java v0.1, 2025-05-10
 */

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookDetailService bookDetailService;

    @PostMapping("/detail/{bookId}")
    public ApiResponse<BookDetailResponse> getBookDetail(@PathVariable("bookId") Long bookId) {
        return ApiResponse.<BookDetailResponse>builder()
                .result(BookDetailResponse.builder().build())
                .build();
    }

}
