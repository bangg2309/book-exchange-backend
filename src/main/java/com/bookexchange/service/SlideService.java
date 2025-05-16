package com.bookexchange.service;

import com.bookexchange.dto.response.SlideResponse;
import com.bookexchange.mapper.SlideMapper;
import com.bookexchange.repository.SlideImageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
        return slideImageRepository.findAll().stream().map(slideMapper::toSlideResponse).toList();

    }
}
