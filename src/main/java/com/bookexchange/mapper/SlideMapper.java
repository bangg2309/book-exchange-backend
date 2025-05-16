package com.bookexchange.mapper;

import com.bookexchange.dto.response.SlideResponse;
import com.bookexchange.entity.Slide;
import org.springframework.stereotype.Component;

@Component
public class SlideMapper {
    public SlideResponse toSlideResponse(Slide slideImage){
        return SlideResponse.builder().imageUrl(slideImage.getImageUrl()).build();
    }
}
