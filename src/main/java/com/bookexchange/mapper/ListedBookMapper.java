package com.bookexchange.mapper;

import com.bookexchange.dto.response.AuthorResponse;
import com.bookexchange.dto.response.BookDetailResponse;
import com.bookexchange.dto.response.ImageResponse;
import com.bookexchange.dto.response.ListedBooksResponse;
import com.bookexchange.entity.ListedBook;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
@RequiredArgsConstructor
public class ListedBookMapper {

    private final UserMapper userMapper;

    public ListedBooksResponse toListedBooksResponse(ListedBook listedBook) {
        return ListedBooksResponse.builder()
                .id(listedBook.getId())
                .title(listedBook.getTitle())
                .conditionNumber(listedBook.getConditionNumber())
                .priceNew(listedBook.getPriceNew())
                .price(listedBook.getPrice())
                .description(listedBook.getDescription())
                .fullName(listedBook.getSeller().getFullName())
                .schoolName(listedBook.getSchool().getName())
                .thumbnail(listedBook.getThumbnail())
                .build();
    }

    public BookDetailResponse toBookDetailResponse(ListedBook listedBook) {
        BookDetailResponse response = BookDetailResponse.builder()
                .id(listedBook.getId())
                .conditionDescription(listedBook.getConditionDescription())
                .conditionNumber(listedBook.getConditionNumber())
                .createdAt(listedBook.getCreatedAt())
                .updatedAt(listedBook.getUpdatedAt())
                .description(listedBook.getDescription())
                .inPerson(listedBook.getInPerson())
                .language(listedBook.getLanguage())
                .pageCount(listedBook.getPageCount())
                .price(listedBook.getPrice() != null ? listedBook.getPrice().doubleValue() : null)
                .priceNew(listedBook.getPriceNew() != null ? listedBook.getPriceNew().doubleValue() : null)
                .publishYear(listedBook.getPublishYear())
                .publisher(listedBook.getPublisher())
                .status(listedBook.getStatus())
                .title(listedBook.getTitle())
                .schoolId(listedBook.getSchool() != null ? listedBook.getSchool().getId() : null)
                .sellerId(listedBook.getSeller() != null ? listedBook.getSeller().getId() : null)
                .thumbnail(listedBook.getThumbnail())
                .address(listedBook.getAddress())
                .isbn(listedBook.getIsbn())
                .build();
        if (listedBook.getAuthors() != null) {
            response.setAuthors(listedBook.getAuthors().stream()
                    .map(AuthMapper::fromEntity2Response)
                    .toList());
        }
        if (listedBook.getImages() != null) {
            response.setImages(listedBook.getImages().stream()
                    .map(ImageMapper::fromEntity2Response)
                    .toList());
        }
        if(listedBook.getReviews() != null) {
            response.setReviews(listedBook.getReviews().stream()
                    .map(ReviewMapper::fromEntity2Response)
                    .toList());
        }
        if (listedBook.getSchool() != null) {
            response.setSchool(SchoolMapper.fromEntity2Response(listedBook.getSchool()));
        }
        if (listedBook.getSeller() != null) {
            response.setSeller(userMapper.toUserResponse(listedBook.getSeller()));
        }
        return response;
    }

}
