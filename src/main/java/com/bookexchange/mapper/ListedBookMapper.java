package com.bookexchange.mapper;

import com.bookexchange.dto.response.ListedBooksResponse;
import com.bookexchange.entity.ListedBook;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
}
