package com.bookexchange.controller;

import com.bookexchange.dto.request.VNPayRequest;
import com.bookexchange.dto.response.PaymentUrlResponse;
import com.bookexchange.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    VNPayService vnPayService;

    /**
     * Tạo URL thanh toán cho VNPay
     * 
     * @param request thông tin đơn hàng cần thanh toán
     * @param servletRequest request từ client
     * @return URL thanh toán VNPay
     */
    @PostMapping("/create-payment")
    public ResponseEntity<PaymentUrlResponse> createPayment(
            @RequestBody VNPayRequest request,
            HttpServletRequest servletRequest) {
        
        // Lấy IP client
        String ipAddress = servletRequest.getRemoteAddr();
        request.setIpAddress(ipAddress);
        
        // Tạo URL thanh toán
        PaymentUrlResponse response = vnPayService.createPaymentUrl(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint xử lý callback từ VNPay
     * 
     * @param servletRequest request từ VNPay
     * @return kết quả xử lý
     */
    @GetMapping("/vnpay-callback")
    public ResponseEntity<Map<String, String>> vnpayCallback(HttpServletRequest servletRequest) {
        Map<String, String> vnpParams = new HashMap<>();
        
        // Lấy tất cả tham số từ VNPay
        Enumeration<String> paramNames = servletRequest.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = servletRequest.getParameter(paramName);
            if (paramValue != null && !paramValue.isEmpty()) {
                vnpParams.put(paramName, paramValue);
            }
        }
        
        // Ghi log các tham số nhận được
        log.info("VNPay Callback Params: {}", vnpParams);
        
        // Xử lý callback
        Map<String, String> result = vnPayService.processPaymentCallback(vnpParams);
        
        return ResponseEntity.ok(result);
    }
} 