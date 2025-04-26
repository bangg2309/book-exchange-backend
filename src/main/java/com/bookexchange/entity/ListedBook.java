package com.bookexchange.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "listed_books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListedBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String title;

    String author;

    String thumnail;
    String publisher;
    String publishYear;
    String language;

    @Column(columnDefinition = "TEXT")
    String description;

    Integer pageCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    User seller;

    Integer conditionNumber;

    @Column(columnDefinition = "TEXT")
    String conditionDescription;

    BigDecimal price;
    BigDecimal discount;
    Integer status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    School school;

    Integer inPerson;

    String image;

    @ManyToMany
    @JoinTable(
            name = "book_categories",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    Set<OrderItem> orderItems = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    Set<Review> reviews = new HashSet<>();

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;
}