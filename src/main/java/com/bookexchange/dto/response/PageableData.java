package com.bookexchange.dto.response;


import lombok.Builder;
import lombok.Data;

/**
 * @author Nguyen Toan
 * @version PageableData.java v0.1, 2025-05-17
 */

@Data
@Builder
public class PageableData {
    private int totalPages;
    private long totalElements;
    private Object data;
}
