package com.bookexchange.dto.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Nguyen Toan
 * @version BookDetailResponse.java v0.1, 2025-05-10
 */

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookDetailResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 8659085094297482465L;

    private Long id;
    private String conditionDescription;
    private Integer conditionNumber;
    private LocalDateTime createdAt;
    private String description;
    private BigDecimal discount;
    private Integer inPerson;
    private String language;
    private Integer pageCount;
    private Double price;
    private String publishYear;
    private String publisher;
    private Integer status;
    private String title;
    private LocalDateTime updatedAt;

    private Long schoolId;
    private SchoolResponse school;

    private Long sellerId;
    private UserResponse seller;

    private Double priceNew;
    private String thumbnail;
    private String address;
    private String isbn;

    private List<AuthorResponse> authors;
    private List<ImageResponse> images;
    private List<ReviewResponse> reviews;

}
