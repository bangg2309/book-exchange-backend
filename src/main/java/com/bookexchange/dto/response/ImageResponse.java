package com.bookexchange.dto.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Nguyen Toan
 * @version ImageResponse.java v0.1, 2025-05-10
 */

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -1241594072180438833L;

    private Long id;
    private String url;
    private Long bookId;
}
