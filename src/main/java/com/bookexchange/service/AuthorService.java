package com.bookexchange.service;

import com.bookexchange.dto.response.AuthorResponse;
import com.bookexchange.repository.AuthorRepository;
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
public class AuthorService {
    AuthorRepository authorRepository;

    public List<AuthorResponse> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(author -> AuthorResponse.builder()
                        .id(author.getId())
                        .name(author.getName())
                        .build())
                .collect(toList());
    }
}
