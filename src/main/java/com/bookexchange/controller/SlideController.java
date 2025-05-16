package com.bookexchange.controller;

import com.bookexchange.dto.request.ApiResponse;
import com.bookexchange.dto.response.SlideResponse;
import com.bookexchange.service.SlideService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/slides")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SlideController {
    SlideService slideService;

    @GetMapping
    public ApiResponse<List<SlideResponse>> getAllSlideImages() {
        return ApiResponse.<List<SlideResponse>>builder().result(slideService.getAllSlide()).build();
    }

}
