package com.bookexchange.controller;

import com.bookexchange.dto.request.ApiResponse;
import com.bookexchange.dto.request.SlideRequest;
import com.bookexchange.dto.response.SlideResponse;
import com.bookexchange.service.SlideService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@RestController
@RequestMapping("/slides")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SlideController {
    SlideService slideService;

    @GetMapping
    public ApiResponse<Page<SlideResponse>> getSlides(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<Page<SlideResponse>>builder()
                .result(slideService.getSlides(PageRequest.of(page, size)))
                .build();
    }

//    @GetMapping
//    public ApiResponse<List<SlideResponse>> getSlideImages() {
//        return ApiResponse.<List<SlideResponse>>builder().result(slideService.getSlideImages()).build();
//    }

    @DeleteMapping("/{slideId}")
    public ApiResponse<String> deleteSlide(@PathVariable String slideId) {
        slideService.deleteSlide(slideId);
        return ApiResponse.<String>builder().result("Slide has been deleted").build();
    }
    @PutMapping("/{slideId}")
    public ApiResponse<SlideResponse> updateSlide(@PathVariable String slideId,
                                                  @RequestBody SlideRequest request) {
        return ApiResponse.<SlideResponse>builder()
                .result(slideService.updateSlide(slideId, request))
                .build();
    }



}
