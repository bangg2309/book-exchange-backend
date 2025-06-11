package com.bookexchange.controller;

import com.bookexchange.dto.request.VoucherRequest;
import com.bookexchange.dto.response.VoucherResponse;
import com.bookexchange.service.VoucherService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherController {
    VoucherService voucherService;

    @PostMapping
    public ResponseEntity<VoucherResponse> createVoucher(@RequestBody @Valid VoucherRequest request) {
        VoucherResponse voucher = voucherService.createVoucher(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(voucher);
    }

    @GetMapping("/platform")
    public ResponseEntity<List<VoucherResponse>> getPlatformVouchers() {
        List<VoucherResponse> vouchers = voucherService.getPlatformVouchers();
        return ResponseEntity.ok(vouchers);
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<VoucherResponse>> getSellerVouchers(@PathVariable Long sellerId) {
        List<VoucherResponse> vouchers = voucherService.getSellerVouchers(sellerId);
        return ResponseEntity.ok(vouchers);
    }

    @GetMapping("/{code}")
    public ResponseEntity<VoucherResponse> getVoucherByCode(@PathVariable String code) {
        VoucherResponse voucher = voucherService.getVoucherByCode(code);
        return ResponseEntity.ok(voucher);
    }

    @PostMapping("/{code}/validate")
    public ResponseEntity<?> validateVoucher(@PathVariable String code, @RequestParam BigDecimal orderValue) {
        BigDecimal discount = voucherService.validateAndCalculateDiscount(code, orderValue);
        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("discount", discount);
        return ResponseEntity.ok(response);
    }

    /**
     * Initialize FIRSTBUY voucher (for demo purposes)
     */
    @PostMapping("/initialize-demo")
    public ResponseEntity<VoucherResponse> initializeDemoVoucher() {
        // Check if the voucher already exists
        try {
            voucherService.getVoucherByCode("FIRSTBUY");
            return ResponseEntity.ok().build(); // Voucher already exists
        } catch (Exception e) {
            // Create demo voucher
            VoucherRequest request = new VoucherRequest();
            request.setCode("FIRSTBUY");
            request.setDescription("Giảm 10% cho đơn hàng đầu tiên, tối đa 50.000đ");
            request.setDiscountPercentage(10);
            request.setMaxDiscount(new BigDecimal("50000"));
            request.setMinOrderValue(new BigDecimal("0"));
            request.setMaxUses(50);
            request.setStartsAt(LocalDateTime.now());
            request.setExpiresAt(LocalDateTime.now().plusMonths(3));
            
            VoucherResponse voucher = voucherService.createVoucher(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(voucher);
        }
    }
} 