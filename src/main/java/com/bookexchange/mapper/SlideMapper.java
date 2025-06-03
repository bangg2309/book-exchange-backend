package com.bookexchange.mapper;

import com.bookexchange.dto.response.SlideResponse;
import com.bookexchange.entity.Slide;
import org.springframework.stereotype.Component;

@Component
public class SlideMapper {

    public SlideResponse toSlideResponse(Slide slideImage) {
        return SlideResponse.builder()
                .id(slideImage.getId())
                .imageUrl(slideImage.getImageUrl())
                .addedBy(slideImage.getAddedBy() != null ? slideImage.getAddedBy().getUsername() : null)
                .addedAt(slideImage.getAddedAt())
                .event(slideImage.getEvent())
                .status(slideImage.getStatus()) // thêm status
                .build();
    }
}
