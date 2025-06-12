package com.bookexchange.service;

import com.bookexchange.config.VNPayConfig;
import com.bookexchange.dto.request.VNPayRequest;
import com.bookexchange.dto.response.PaymentUrlResponse;
import com.bookexchange.entity.Order;
import com.bookexchange.exception.AppException;
import com.bookexchange.exception.ErrorCode;
import com.bookexchange.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VNPayService {
    VNPayConfig vnPayConfig;
    OrderRepository orderRepository;

    /**
     * Tạo URL thanh toán VNPay
     *
     * @param request thông tin thanh toán
     * @return URL thanh toán
     */
    public PaymentUrlResponse createPaymentUrl(VNPayRequest request) {
        // Kiểm tra đơn hàng tồn tại
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        try {
            Map<String, String> vnpParams = new HashMap<>();

            // Các tham số bắt buộc theo thứ tự alphabet
            vnpParams.put("vnp_Version", vnPayConfig.getVnpVersion());
            vnpParams.put("vnp_Command", vnPayConfig.getVnpCommand());
            vnpParams.put("vnp_TmnCode", vnPayConfig.getVnpTmnCode());

            // Số tiền thanh toán * 100 (VNPay yêu cầu số tiền tính bằng đơn vị xu)
            long amount = request.getAmount().multiply(new java.math.BigDecimal("100")).longValue();
            vnpParams.put("vnp_Amount", String.valueOf(amount));

            // Mã tiền tệ - BẮT BUỘC
            vnpParams.put("vnp_CurrCode", "VND");

            // Mã đơn hàng - kết hợp orderId + timestamp để tạo mã duy nhất
            String vnp_TxnRef = request.getOrderId() + "-" + System.currentTimeMillis();
            vnpParams.put("vnp_TxnRef", vnp_TxnRef);

            vnpParams.put("vnp_OrderInfo", request.getOrderInfo());
            vnpParams.put("vnp_OrderType", "other"); // Mã danh mục hàng hóa

            // Ngôn ngữ hiển thị thanh toán
            String locale = request.getLanguage();
            if (locale == null || locale.isEmpty()) {
                locale = "vn"; // Mặc định tiếng Việt
            }
            vnpParams.put("vnp_Locale", locale);

            // URL return sau khi thanh toán
            String returnUrl = request.getReturnUrl();
            if (returnUrl == null || returnUrl.isEmpty()) {
                returnUrl = vnPayConfig.getVnpReturnUrl();
            }
            vnpParams.put("vnp_ReturnUrl", returnUrl);

            // IP của khách hàng - xử lý đặc biệt cho IPv6
            String ipAddress = request.getIpAddress();
            if ("0:0:0:0:0:0:0:1".equals(ipAddress) || "::1".equals(ipAddress)) {
                ipAddress = "127.0.0.1"; // Chuyển localhost IPv6 thành IPv4
            }
            vnpParams.put("vnp_IpAddr", ipAddress);

            // Ngày tạo giao dịch
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnpParams.put("vnp_CreateDate", vnp_CreateDate);

            // Ngày giao dịch hết hạn
            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnpParams.put("vnp_ExpireDate", vnp_ExpireDate);

            // Thêm bankcode nếu được chỉ định
            if (request.getBankCode() != null && !request.getBankCode().isEmpty()) {
                vnpParams.put("vnp_BankCode", request.getBankCode());
            }

            // Tạo URL thanh toán
            List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnpParams.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    // Build hash data - SỬ DỤNG UTF-8
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));

                    // Build query - SỬ DỤNG UTF-8
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));

                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }

            String vnp_SecureHash = vnPayConfig.hmacSHA512(vnPayConfig.getVnpHashSecret(), hashData.toString());
            query.append("&vnp_SecureHash=").append(vnp_SecureHash);

            String paymentUrl = vnPayConfig.getVnpPayUrl() + "?" + query;

            // Ghi log chi tiết để debug
            log.info("VNPay Payment Parameters:");
            vnpParams.forEach((key, value) -> log.info("{}: {}", key, value));
            log.info("Hash Data: {}", hashData.toString());
            log.info("VNPay Payment URL: {}", paymentUrl);

            // Tạo response
            return PaymentUrlResponse.builder()
                    .code("00")
                    .message("success")
                    .paymentUrl(paymentUrl)
                    .build();

        } catch (Exception e) {
            log.error("Error when creating VNPay payment URL: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể tạo URL thanh toán", e);
        }
    }

    /**
     * Xử lý callback từ VNPay sau khi thanh toán
     */
    public Map<String, String> processPaymentCallback(Map<String, String> vnpParams) {
        Map<String, String> result = new HashMap<>();

        try {
            log.info("Processing VNPay callback with parameters:");
            vnpParams.forEach((key, value) -> log.info("{}: {}", key, value));

            if (validateSignature(vnpParams)) {
                String vnp_TxnRef = vnpParams.get("vnp_TxnRef");

                // Xử lý orderId từ vnp_TxnRef
                Long orderId;
                try {
                    orderId = Long.valueOf(vnp_TxnRef.split("-")[0]);
                } catch (Exception e) {
                    log.error("Invalid vnp_TxnRef format: {}", vnp_TxnRef);
                    result.put("code", "01");
                    result.put("message", "Mã giao dịch không hợp lệ");
                    return result;
                }

                String vnp_ResponseCode = vnpParams.get("vnp_ResponseCode");
                Optional<Order> orderOpt = orderRepository.findById(orderId);

                if (orderOpt.isPresent()) {
                    Order order = orderOpt.get();

                    if ("00".equals(vnp_ResponseCode)) {
                        order.setStatus(2); // Đã thanh toán
                        orderRepository.save(order);

                        result.put("code", "00");
                        result.put("message", "Thanh toán thành công");
                        log.info("Payment successful for order: {}", orderId);
                    } else {
                        result.put("code", vnp_ResponseCode);
                        result.put("message", "Thanh toán không thành công");
                        log.warn("Payment failed for order: {} with code: {}", orderId, vnp_ResponseCode);
                    }
                } else {
                    result.put("code", "01");
                    result.put("message", "Không tìm thấy đơn hàng");
                    log.error("Order not found: {}", orderId);
                }
            } else {
                result.put("code", "97");
                result.put("message", "Chữ ký không hợp lệ");
                log.error("Invalid signature in VNPay callback");
            }
        } catch (Exception e) {
            log.error("Error when processing VNPay callback: {}", e.getMessage(), e);
            result.put("code", "99");
            result.put("message", "Lỗi xử lý thanh toán");
        }

        return result;
    }

    /**
     * Xác thực chữ ký điện tử từ VNPay
     */
    private boolean validateSignature(Map<String, String> vnpParams) {
        try {
            String vnp_SecureHash = vnpParams.get("vnp_SecureHash");
            if (vnp_SecureHash == null || vnp_SecureHash.isEmpty()) {
                log.error("Missing vnp_SecureHash in callback");
                return false;
            }

            // Tạo bản copy và xóa các tham số không cần thiết cho việc tính hash
            Map<String, String> signParams = new HashMap<>(vnpParams);
            signParams.remove("vnp_SecureHash");
            signParams.remove("vnp_SecureHashType");

            // Tính toán lại secure hash
            String signValue = vnPayConfig.hashAllFields(signParams);

            log.info("Expected hash: {}", signValue);
            log.info("Received hash: {}", vnp_SecureHash);

            return signValue.equals(vnp_SecureHash);
        } catch (Exception e) {
            log.error("Error validating signature: {}", e.getMessage(), e);
            return false;
        }
    }
}