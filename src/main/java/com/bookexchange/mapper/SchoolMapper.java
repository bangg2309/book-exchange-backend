package com.bookexchange.mapper;


import com.bookexchange.dto.response.SchoolResponse;
import com.bookexchange.entity.School;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Nguyen Toan
 * @version SchoolMapper.java v0.1, 2025-05-11
 */

@Component
public class SchoolMapper {
    public static SchoolResponse fromEntity2Response(School school) {
        return SchoolResponse.builder()
                .id(school.getId())
                .name(school.getName())
                .address(school.getAddress())
                .build();
    }
}
