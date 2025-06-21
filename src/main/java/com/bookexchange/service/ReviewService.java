package com.bookexchange.service;

import com.bookexchange.dto.request.ReviewRequest;
import com.bookexchange.dto.response.ReviewResponse;
import com.bookexchange.entity.ListedBook;
import com.bookexchange.entity.Review;
import com.bookexchange.entity.User;
import com.bookexchange.exception.AppException;
import com.bookexchange.exception.ErrorCode;
import com.bookexchange.mapper.ReviewMapper;
import com.bookexchange.repository.ListedBookRepository;
import com.bookexchange.repository.ReviewRepository;
import com.bookexchange.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService {

    ReviewRepository reviewRepository;
    UserRepository userRepository;
    ListedBookRepository listedBookRepository;
    ReviewMapper reviewMapper;

    @Transactional
    public ReviewResponse createReview(ReviewRequest request) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();
        log.info("Request to create review: {}", request);

        User reviewer = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        User seller = userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        ListedBook book = listedBookRepository.findById(request.getBookId())
                .orElseThrow(() -> new AppException(ErrorCode.LISTED_BOOK_NOT_FOUND));

        // Check if user has already reviewed this book
        reviewRepository.findByBookAndReviewer(book, reviewer).ifPresent(existingReview -> {
            throw new AppException(ErrorCode.REVIEW_ALREADY_EXISTS);
        });

        Review review = new Review();
        review.setReviewer(reviewer);
        review.setSeller(seller);
        review.setBook(book);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toReviewResponse(savedReview);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByBook(Long bookId) {
        ListedBook book = listedBookRepository.findById(bookId)
                .orElseThrow(() -> new AppException(ErrorCode.LISTED_BOOK_NOT_FOUND));

        return reviewRepository.findByBook(book).stream()
                .map(reviewMapper::toReviewResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsBySeller(Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return reviewRepository.findBySeller(seller).stream()
                .map(reviewMapper::toReviewResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        return reviewMapper.toReviewResponse(review);
    }

    /**
     * Lấy đánh giá của người dùng hiện tại cho một cuốn sách cụ thể
     */
    @Transactional(readOnly = true)
    public ReviewResponse getUserReviewForBook(Long bookId) {
        try {
            var context = SecurityContextHolder.getContext();
            String username = context.getAuthentication().getName();

            User reviewer = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            ListedBook book = listedBookRepository.findById(bookId)
                    .orElseThrow(() -> new AppException(ErrorCode.LISTED_BOOK_NOT_FOUND));

            Optional<Review> reviewOptional = reviewRepository.findByBookAndReviewer(book, reviewer);
            
            if (reviewOptional.isPresent()) {
                return reviewMapper.toReviewResponse(reviewOptional.get());
            }
            
            return null;
        } catch (Exception e) {
            log.error("Error getting user review for book: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Kiểm tra xem một người dùng cụ thể đã đánh giá một cuốn sách hay chưa
     */
    @Transactional(readOnly = true)
    public ReviewResponse getReviewByBookAndUser(Long bookId, Long userId) {
        try {
            User reviewer = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            ListedBook book = listedBookRepository.findById(bookId)
                    .orElseThrow(() -> new AppException(ErrorCode.LISTED_BOOK_NOT_FOUND));

            Optional<Review> reviewOptional = reviewRepository.findByBookAndReviewer(book, reviewer);
            
            if (reviewOptional.isPresent()) {
                return reviewMapper.toReviewResponse(reviewOptional.get());
            }
            
            return null;
        } catch (Exception e) {
            log.error("Error checking if review exists: {}", e.getMessage());
            return null;
        }
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();
        log.info("Request to update review: {}", request);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        User reviewer = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Check if the reviewer is the owner of the review
        if (review.getReviewer().getId() != reviewer.getId()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updatedReview = reviewRepository.save(review);
        return reviewMapper.toReviewResponse(updatedReview);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        User reviewer = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Check if the reviewer is the owner of the review
        if (review.getReviewer().getId() != reviewer.getId()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        reviewRepository.delete(review);
    }
} 