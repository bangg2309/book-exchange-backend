package com.bookexchange.controller;

import com.bookexchange.dto.request.ApiResponse;
import com.bookexchange.dto.request.ReviewRequest;
import com.bookexchange.dto.response.ReviewResponse;
import com.bookexchange.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ReviewController {
    
    ReviewService reviewService;
    
    /**
     * Create a new review
     */
    @PostMapping
    public ApiResponse<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest request) {
        ReviewResponse reviewResponse = reviewService.createReview(request);
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewResponse)
                .build();
    }
    
    /**
     * Get a review by ID
     */
    @GetMapping("/{reviewId}")
    public ApiResponse<ReviewResponse> getReview(@PathVariable Long reviewId) {
        ReviewResponse reviewResponse = reviewService.getReview(reviewId);
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewResponse)
                .build();
    }
    
    /**
     * Get reviews for a book
     */
    @GetMapping("/book/{bookId}")
    public ApiResponse<List<ReviewResponse>> getReviewsByBook(@PathVariable Long bookId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByBook(bookId);
        return ApiResponse.<List<ReviewResponse>>builder()
                .result(reviews)
                .build();
    }
    
    /**
     * Get reviews for a seller
     */
    @GetMapping("/seller/{sellerId}")
    public ApiResponse<List<ReviewResponse>> getReviewsBySeller(@PathVariable Long sellerId) {
        List<ReviewResponse> reviews = reviewService.getReviewsBySeller(sellerId);
        return ApiResponse.<List<ReviewResponse>>builder()
                .result(reviews)
                .build();
    }
    
    /**
     * Check if a review exists for a book by current user
     */
    @GetMapping("/user/book/{bookId}")
    public ApiResponse<ReviewResponse> getUserReviewForBook(@PathVariable Long bookId) {
        ReviewResponse review = reviewService.getUserReviewForBook(bookId);
        return ApiResponse.<ReviewResponse>builder()
                .result(review)
                .build();
    }
    
    /**
     * Check if a review exists for a book by specific user
     */
    @GetMapping("/check")
    public ApiResponse<ReviewResponse> checkReviewExists(
            @RequestParam Long bookId,
            @RequestParam Long userId) {
        ReviewResponse review = reviewService.getReviewByBookAndUser(bookId, userId);
        return ApiResponse.<ReviewResponse>builder()
                .result(review)
                .build();
    }
    
    /**
     * Update a review
     */
    @PutMapping("/{reviewId}")
    public ApiResponse<ReviewResponse> updateReview(@PathVariable Long reviewId, 
                                                  @Valid @RequestBody ReviewRequest request) {
        ReviewResponse reviewResponse = reviewService.updateReview(reviewId, request);
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewResponse)
                .build();
    }
    
    /**
     * Delete a review
     */
    @DeleteMapping("/{reviewId}")
    public ApiResponse<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ApiResponse.<Void>builder()
                .build();
    }
} 