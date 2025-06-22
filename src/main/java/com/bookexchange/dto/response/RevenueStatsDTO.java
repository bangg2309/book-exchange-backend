package com.bookexchange.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueStatsDTO {
    private List<String> labels; // Nhãn thời gian (ngày, tuần, tháng, năm)
    private List<Double> data; // Dữ liệu doanh thu (đơn vị: nghìn đồng)
    private String period; // Khoảng thời gian (day, week, month, year)
} 