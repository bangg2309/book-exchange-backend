package com.bookexchange.mapper;


import com.bookexchange.dto.response.ImageResponse;
import com.bookexchange.entity.Image;

/**
 * @author Nguyen Toan
 * @version ImageMapper.java v0.1, 2025-05-11
 */

public class ImageMapper {

    public static ImageResponse fromEntity2Response(Image image) {
        return ImageResponse.builder()
                .id(image.getId())
                .url(image.getImageUrl())
                .build();
    }

}
