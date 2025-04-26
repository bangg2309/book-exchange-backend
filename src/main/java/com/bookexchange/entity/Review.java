package com.bookexchange.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    User reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    ListedBook book;

    Integer rating;

    @Column(columnDefinition = "TEXT")
    String comment;

    @CreationTimestamp
    LocalDateTime createdAt;
}