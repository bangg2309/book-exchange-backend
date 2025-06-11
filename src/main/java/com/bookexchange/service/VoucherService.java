package com.bookexchange.service;

import com.bookexchange.dto.request.VoucherRequest;
import com.bookexchange.dto.response.VoucherResponse;
import com.bookexchange.entity.User;
import com.bookexchange.entity.Voucher;
import com.bookexchange.exception.AppException;
import com.bookexchange.exception.ErrorCode;
import com.bookexchange.repository.UserRepository;
import com.bookexchange.repository.VoucherRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherService {
    VoucherRepository voucherRepository;
    UserRepository userRepository;

    /**
     * Create a new voucher
     * @param request The voucher creation request
     * @return The created voucher
     */
    @Transactional
    public VoucherResponse createVoucher(VoucherRequest request) {
        // Check if voucher code already exists
        if (voucherRepository.findByCode(request.getCode()).isPresent()) {
            throw new AppException(ErrorCode.VOUCHER_CODE_ALREADY_EXISTS);
        }

        Voucher voucher = new Voucher();
        voucher.setCode(request.getCode());
        voucher.setDescription(request.getDescription());
        voucher.setDiscountAmount(request.getDiscountAmount());
        voucher.setDiscountPercentage(request.getDiscountPercentage());
        voucher.setMinOrderValue(request.getMinOrderValue());
        voucher.setMaxDiscount(request.getMaxDiscount());
        voucher.setStartsAt(request.getStartsAt());
        voucher.setExpiresAt(request.getExpiresAt());
        voucher.setMaxUses(request.getMaxUses());
        voucher.setCurrentUses(0);

        // Set seller if it's a seller voucher
        if (request.getSellerId() != null) {
            User seller = userRepository.findById(request.getSellerId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            voucher.setSeller(seller);
        }

        Voucher savedVoucher = voucherRepository.save(voucher);
        return toVoucherResponse(savedVoucher);
    }

    /**
     * Get all platform vouchers
     * @return List of platform vouchers
     */
    public List<VoucherResponse> getPlatformVouchers() {
        return voucherRepository.findAll().stream()
                .filter(voucher -> voucher.getSeller() == null)
                .map(this::toVoucherResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get vouchers for a seller
     * @param sellerId The seller ID
     * @return List of seller vouchers
     */
    public List<VoucherResponse> getSellerVouchers(Long sellerId) {
        return voucherRepository.findAll().stream()
                .filter(voucher -> voucher.getSeller() != null && voucher.getSeller().getId() == (sellerId))
                .map(this::toVoucherResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a voucher by code
     * @param code The voucher code
     * @return The voucher if exists
     */
    public VoucherResponse getVoucherByCode(String code) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        return toVoucherResponse(voucher);
    }

    /**
     * Validate a voucher for an order
     * @param code The voucher code
     * @param orderValue The order value
     * @return The discount amount
     */
    public BigDecimal validateAndCalculateDiscount(String code, BigDecimal orderValue) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();

        // Check if voucher is valid
        if (now.isBefore(voucher.getStartsAt()) || now.isAfter(voucher.getExpiresAt())) {
            throw new AppException(ErrorCode.VOUCHER_EXPIRED);
        }

        // Check if voucher has reached max uses
        if (voucher.getMaxUses() != null && voucher.getCurrentUses() >= voucher.getMaxUses()) {
            throw new AppException(ErrorCode.VOUCHER_MAX_USES_REACHED);
        }

        // Check minimum order value
        if (voucher.getMinOrderValue() != null && orderValue.compareTo(voucher.getMinOrderValue()) < 0) {
            throw new AppException(ErrorCode.ORDER_VALUE_TOO_LOW);
        }

        // Calculate discount
        BigDecimal discount;
        if (voucher.getDiscountAmount() != null) {
            discount = voucher.getDiscountAmount();
        } else if (voucher.getDiscountPercentage() != null) {
            discount = orderValue.multiply(BigDecimal.valueOf(voucher.getDiscountPercentage()).divide(BigDecimal.valueOf(100)));
            
            // Apply max discount if specified
            if (voucher.getMaxDiscount() != null && discount.compareTo(voucher.getMaxDiscount()) > 0) {
                discount = voucher.getMaxDiscount();
            }
        } else {
            throw new AppException(ErrorCode.INVALID_VOUCHER);
        }

        return discount;
    }

    /**
     * Apply a voucher to an order (increment usage count)
     * @param code The voucher code
     */
    @Transactional
    public void applyVoucher(String code) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        
        voucher.setCurrentUses(voucher.getCurrentUses() + 1);
        voucherRepository.save(voucher);
    }

    /**
     * Convert Voucher entity to VoucherResponse DTO
     * @param voucher The voucher entity
     * @return The voucher response
     */
    private VoucherResponse toVoucherResponse(Voucher voucher) {
        VoucherResponse.VoucherResponseBuilder builder = VoucherResponse.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .description(voucher.getDescription())
                .discountAmount(voucher.getDiscountAmount())
                .discountPercentage(voucher.getDiscountPercentage())
                .minOrderValue(voucher.getMinOrderValue())
                .maxDiscount(voucher.getMaxDiscount())
                .startsAt(voucher.getStartsAt())
                .expiresAt(voucher.getExpiresAt())
                .maxUses(voucher.getMaxUses())
                .currentUses(voucher.getCurrentUses())
                .isPlatformVoucher(voucher.getSeller() == null)
                .createdAt(voucher.getCreatedAt());

        if (voucher.getSeller() != null) {
            builder.sellerId(voucher.getSeller().getId());
            builder.sellerName(voucher.getSeller().getFullName());
        }

        return builder.build();
    }
} 