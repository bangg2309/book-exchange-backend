package com.bookexchange.mapper;

import com.bookexchange.dto.response.AuthorResponse;
import com.bookexchange.dto.response.SlideResponse;
import com.bookexchange.entity.Author;
import com.bookexchange.entity.Slide;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public AuthorResponse toAuthorResponse(Author author) {
        return AuthorResponse.builder()
                .id(author.getId())
                .name(author.getName())
                .build();
    }
}
