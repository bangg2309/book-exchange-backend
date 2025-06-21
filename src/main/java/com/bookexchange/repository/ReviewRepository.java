package com.bookexchange.repository;

import com.bookexchange.entity.ListedBook;
import com.bookexchange.entity.Review;
import com.bookexchange.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBook(ListedBook book);
    List<Review> findBySeller(User seller);
    List<Review> findByReviewer(User reviewer);
    Optional<Review> findByBookAndReviewer(ListedBook book, User reviewer);
} 