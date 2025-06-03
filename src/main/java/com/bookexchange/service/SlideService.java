package com.bookexchange.service;

import com.bookexchange.dto.request.SlideRequest;
import com.bookexchange.dto.response.SlideResponse;
import com.bookexchange.entity.Slide;
import com.bookexchange.mapper.SlideMapper;
import com.bookexchange.repository.SlideImageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SlideService {

    SlideImageRepository slideImageRepository;
    SlideMapper slideMapper;

    public List<SlideResponse> getAllSlide() {
        return slideImageRepository.findAll().stream()
                .map(slideMapper::toSlideResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteSlide(String slideId) {
        long id = Long.parseLong(slideId);
        if (!slideImageRepository.existsById(slideId)) {
            throw new RuntimeException("Slide not found with ID: " + slideId);
        }
        slideImageRepository.deleteById(slideId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public SlideResponse updateSlide(String slideId, SlideRequest request) {
        long id = Long.parseLong(slideId);
        Slide slide = slideImageRepository.findById(slideId)
                .orElseThrow(() -> new RuntimeException("Slide not found with ID: " + slideId));

        // Cập nhật dữ liệu
        slide.setImageUrl(request.getImageUrl());
        slide.setEvent(request.getEvent());
        slide.setStatus(request.getStatus());

        // Lưu và trả về response
        Slide updatedSlide = slideImageRepository.save(slide);
        return slideMapper.toSlideResponse(updatedSlide);
    }
}
