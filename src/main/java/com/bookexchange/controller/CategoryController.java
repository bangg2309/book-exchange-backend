package com.bookexchange.controller;

import com.bookexchange.dto.request.ApiResponse;
import com.bookexchange.dto.request.CategoryCreationRequest;
import com.bookexchange.dto.request.CategoryUpdateRequest;
import com.bookexchange.dto.request.UserUpdateRequest;
import com.bookexchange.dto.response.CategoryManagementResponse;
import com.bookexchange.dto.response.CategoryResponse;
import com.bookexchange.dto.response.UserResponse;
import com.bookexchange.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categoryService.getAllCategories())
                .build();
    }

    // Lấy danh sách phân trang danh mục
    @GetMapping("/all")
    public ApiResponse<Page<CategoryManagementResponse>> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<Page<CategoryManagementResponse>>builder()
                .result(categoryService.getCategories(PageRequest.of(page, size)))
                .build();
    }

    // Lấy chi tiết danh mục theo ID
    @GetMapping("/{id}")
    public ApiResponse<CategoryManagementResponse> getCategory(@PathVariable long id) {
        return ApiResponse.<CategoryManagementResponse>builder()
                .result(categoryService.getCategory(id))
                .build();
    }

    // Tạo danh mục mới
    @PostMapping
    public ApiResponse<CategoryManagementResponse> createCategory(
            @RequestBody @Valid CategoryCreationRequest request
    ) {
        return ApiResponse.<CategoryManagementResponse>builder()
                .result(categoryService.createCategory(request))
                .build();
    }

    // Cập nhật danh mục
    @PutMapping("/{id}")
    public ApiResponse<CategoryManagementResponse> updateCategory(@PathVariable long id, @RequestBody CategoryUpdateRequest request) {
        return ApiResponse.<CategoryManagementResponse>builder()
                .result(categoryService.updateCategory(id, request))
                .build();
    }

    // Xoá danh mục
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCategory(@PathVariable long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.<String>builder()
                .result("Category deleted successfully")
                .build();
    }

}
