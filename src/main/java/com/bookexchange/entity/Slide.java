package com.bookexchange.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "slides")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Slide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String imageUrl;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User addedBy;

    @Column(name = "added_at", columnDefinition = "DATETIME")
    @CreationTimestamp
    LocalDateTime addedAt;

    String event;

    @Column(nullable = false)
    int status;
}