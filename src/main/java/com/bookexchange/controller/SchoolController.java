package com.bookexchange.controller;

import com.bookexchange.dto.request.ApiResponse;
import com.bookexchange.dto.response.SchoolResponse;
import com.bookexchange.service.SchoolService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/schools")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SchoolController {

    SchoolService schoolService;

    @GetMapping
    public ApiResponse<List<SchoolResponse>> getAllSchools() {
        return ApiResponse.<List<SchoolResponse>>builder()
                .result(schoolService.getAllSchools())
                .build();
    }
}
