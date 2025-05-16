package com.bookexchange.service;

import com.bookexchange.dto.request.CategoryCreationRequest;
import com.bookexchange.dto.request.CategoryRequest;
import com.bookexchange.dto.request.CategoryUpdateRequest;
import com.bookexchange.dto.response.CategoryManagementResponse;
import com.bookexchange.dto.response.CategoryResponse;
import com.bookexchange.entity.Category;
import com.bookexchange.entity.User;
import com.bookexchange.exception.AppException;
import com.bookexchange.exception.ErrorCode;
import com.bookexchange.mapper.CategoryMapper;
import com.bookexchange.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {

    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll().stream()
                .map(category -> CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryManagementResponse createCategory(CategoryCreationRequest request) {
        Category category = categoryMapper.toCategory(request);
        return categoryMapper.toCategoryManagementResponse(categoryRepository.save(category));
    }
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryManagementResponse getCategory(String id) {
        return categoryMapper.toCategoryManagementResponse(
                categoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    public Page<CategoryManagementResponse> getCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toCategoryManagementResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryManagementResponse updateCategory(String id, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        categoryMapper.updateCategory(category, request);
        return categoryMapper.toCategoryManagementResponse(categoryRepository.save(category));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(String id) {
        if (!categoryRepository.existsById(id)) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        categoryRepository.deleteById(id);
    }


}
