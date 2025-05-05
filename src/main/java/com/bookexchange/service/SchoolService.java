package com.bookexchange.service;

import com.bookexchange.dto.response.SchoolResponse;
import com.bookexchange.repository.SchoolRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SchoolService {
    SchoolRepository schoolRepository;

    public List<SchoolResponse> getAllSchools() {
        return schoolRepository.findAll().stream()
                .map(school -> SchoolResponse.builder()
                        .id(school.getId())
                        .name(school.getName())
                        .build())
                .collect(toList());
    }
}
