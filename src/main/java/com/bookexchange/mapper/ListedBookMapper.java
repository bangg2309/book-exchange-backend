package com.bookexchange.mapper;

import com.bookexchange.dto.response.ListedBookDetailResponse;
import com.bookexchange.dto.response.ListedBooksResponse;
import com.bookexchange.dto.response.ReviewResponse;
import com.bookexchange.entity.Author;
import com.bookexchange.entity.Category;
import com.bookexchange.entity.Image;
import com.bookexchange.entity.ListedBook;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ListedBookMapper {

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

    public ListedBookDetailResponse toListedBookDetailResponse(ListedBook listedBook) {
        return ListedBookDetailResponse.builder()
                .id(listedBook.getId())
                .title(listedBook.getTitle())
                .authors(listedBook.getAuthors().stream().map(Author::getName).collect(Collectors.toSet()))
                .categories(listedBook.getCategories().stream().map(Category::getName).collect(Collectors.toSet()))
                .isbn(listedBook.getIsbn())
                .language(listedBook.getLanguage())
                .publishYear(listedBook.getPublishYear())
                .conditionDescription(listedBook.getConditionDescription())
                .pageCount(String.valueOf(listedBook.getPageCount()))
                .sellerName(listedBook.getSeller().getFullName())
                .address(listedBook.getAddress())
                .priceNew(listedBook.getPriceNew())
                .price(listedBook.getPrice())
                .conditionNumber(listedBook.getConditionNumber())
                .schoolName(listedBook.getSchool().getName())
                .publisher(listedBook.getPublisher())
                .description(listedBook.getDescription())
                .thumbnail(listedBook.getThumbnail())
                .images(listedBook.getImages().stream().map(Image::getImageUrl).collect(Collectors.toSet()))
                .createdAt(listedBook.getCreatedAt())
                .build();
    }
}
