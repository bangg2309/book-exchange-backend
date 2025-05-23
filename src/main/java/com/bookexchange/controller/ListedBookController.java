package com.bookexchange.controller;

import com.bookexchange.dto.request.ApiResponse;
import com.bookexchange.dto.request.ListedBookCreationRequest;
import com.bookexchange.dto.response.ListedBookDetailResponse;
import com.bookexchange.dto.response.ListedBooksResponse;
import com.bookexchange.service.ListedBookService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/listed-books")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ListedBookController {

    ListedBookService listedBookService;

    @PostMapping
    public ApiResponse<Void> createListedBook(@RequestBody @Valid ListedBookCreationRequest request) {
        listedBookService.createListedBook(request);
        return ApiResponse.<Void>builder()
                .build();
    }
    @GetMapping("/latest")
    public ApiResponse<List<ListedBooksResponse>> getLatestListedBooks() {

        return ApiResponse.<List<ListedBooksResponse>>builder()
                .result(listedBookService.getLatestListedBooks())
                .build();
    }
    @GetMapping("{id}")
    public ApiResponse<ListedBookDetailResponse> getListedDetail(@PathVariable Long id) {
        return ApiResponse.<ListedBookDetailResponse>builder()
                .result(listedBookService.getListedDetail(id))
                .build();
    }
}
