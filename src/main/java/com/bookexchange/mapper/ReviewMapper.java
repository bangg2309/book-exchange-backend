package com.bookexchange.mapper;

import com.bookexchange.dto.response.ReviewResponse;
import com.bookexchange.entity.Review;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class ReviewMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ReviewResponse toReviewResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .reviewer(review.getReviewer().getFullName())
                .seller(review.getSeller().getFullName())
                .bookName(review.getBook().getTitle())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt().format(FORMATTER))
                .build();
    }
} 