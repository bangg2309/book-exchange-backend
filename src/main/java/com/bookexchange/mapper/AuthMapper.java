package com.bookexchange.mapper;


import com.bookexchange.dto.response.AuthorResponse;
import com.bookexchange.entity.Author;

/**
 * @author Nguyen Toan
 * @version AuthMapper.java v0.1, 2025-05-11
 */

public class AuthMapper {


    public static AuthorResponse fromEntity2Response(Author author) {
        return AuthorResponse.builder()
                .id(author.getId())
                .name(author.getName())
                .build();
    }

}
