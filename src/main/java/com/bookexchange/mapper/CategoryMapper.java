package com.bookexchange.mapper;

import com.bookexchange.dto.request.CategoryCreationRequest;
import com.bookexchange.dto.request.CategoryRequest;
import com.bookexchange.dto.request.CategoryUpdateRequest;
import com.bookexchange.dto.response.CategoryManagementResponse;
import com.bookexchange.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryMapper {

    // Chuyển từ request DTO sang entity khi tạo mới
    public Category toCategory(CategoryRequest request) {
        if (request == null) return null;

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImage_url());
        return category;
    }

    public Category toCategory(CategoryCreationRequest request) {
        if (request == null) return null;

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return category;
    }

    public CategoryManagementResponse toCategoryCreate(Category category) {
        CategoryManagementResponse categoryManagementResponse = new CategoryManagementResponse();
        categoryManagementResponse.setName(category.getName());
        categoryManagementResponse.setDescription(category.getDescription());
        return categoryManagementResponse;
    }

    // Chuyển từ entity sang response DTO khi trả về client
    public CategoryManagementResponse toCategoryManagementResponse(Category category) {
        CategoryManagementResponse categoryManagementResponse = new CategoryManagementResponse();
        categoryManagementResponse.setId(category.getId());
        categoryManagementResponse.setName(category.getName());
        categoryManagementResponse.setDescription(category.getDescription());
        categoryManagementResponse.setImageUrl(category.getImageUrl());
        categoryManagementResponse.setCreatedAt(category.getCreatedAt());
        categoryManagementResponse.setUpdatedAt(category.getUpdatedAt());
        return categoryManagementResponse;

    }

    // Cập nhật entity từ request DTO
    public void updateCategory(Category category, CategoryUpdateRequest request) {
            category.setName(request.getName());
            category.setDescription(request.getDescription());
            category.setImageUrl(request.getImageUrl());

    }
}
