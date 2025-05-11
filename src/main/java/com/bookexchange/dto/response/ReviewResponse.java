package com.bookexchange.dto.response;


import com.bookexchange.entity.ListedBook;
import com.bookexchange.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * @author Nguyen Toan
 * @version ReviewResponse.java v0.1, 2025-05-11
 */

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewResponse {
    Long id;

    Long reviewerId;
    String reviewerName;

    Long sellerId;
    String sellerName;

    Integer rating;
    String comment;
    LocalDateTime createdAt;
}
