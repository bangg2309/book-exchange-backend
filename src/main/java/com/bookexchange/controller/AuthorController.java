package com.bookexchange.controller;

import com.bookexchange.dto.request.ApiResponse;
import com.bookexchange.dto.request.AuthorRequest;
import com.bookexchange.dto.response.AuthorResponse;
import com.bookexchange.service.AuthorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthorController {

    AuthorService authorService;

    // POST /authors - Tạo tác giả mới chỉ với tên
    @PostMapping
    public ApiResponse<AuthorResponse> createAuthor(@RequestBody AuthorRequest request) {
        return ApiResponse.<AuthorResponse>builder()
                .result(authorService.createAuthor(request))
                .message("Author created successfully")
                .build();
    }

    // GET /authors - Lấy tất cả tác giả
    @GetMapping
    public ApiResponse<Page<AuthorResponse>> getAuthors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<Page<AuthorResponse>>builder()
                .result(authorService.getAuthors(PageRequest.of(page, size)))
                .build();
    }
    // DELETE /authors/{id} - Xoá tác giả theo ID
    @DeleteMapping("/{authorId}")
    public ApiResponse<Void> deleteAuthor(@PathVariable Long authorId) {
        authorService.deleteAuthor(authorId);
        return ApiResponse.<Void>builder()
                .message("Author deleted successfully")
                .build();
    }

    // PUT /authors/{id} - Cập nhật thông tin tác giả
    @PutMapping("/{authorId}")
    public ApiResponse<AuthorResponse> updateAuthor(
            @PathVariable Long authorId,
            @RequestBody AuthorRequest request) {
        return ApiResponse.<AuthorResponse>builder()
                .result(authorService.updateAuthor(authorId, request))
                .message("Author updated successfully")
                .build();
    }
}