package com.bookexchange.controller;

import com.bookexchange.dto.request.ApiResponse;
import com.bookexchange.dto.response.AuthorResponse;
import com.bookexchange.service.AuthorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthorController {

    AuthorService authorService;

    @GetMapping
    public ApiResponse<List<AuthorResponse>> getAllAuthors() {
        return ApiResponse.<List<AuthorResponse>>builder()
                .result(authorService.getAllAuthors())
                .build();
    }
}
