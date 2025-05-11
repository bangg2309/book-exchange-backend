package com.bookexchange.mapper;


import com.bookexchange.dto.response.ReviewResponse;
import com.bookexchange.entity.Review;

/**
 * @author Nguyen Toan
 * @version ReviewMapper.java v0.1, 2025-05-11
 */

public class ReviewMapper {

     public static ReviewResponse fromEntity2Response(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .reviewerId(review.getReviewer().getId())
                .reviewerName(review.getReviewer().getFullName())


                .sellerId(review.getSeller().getId())
                .sellerName(review.getSeller().getFullName())

                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
     }
}
