package com.bookexchange.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true)
    String code;

    String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    User seller;

    BigDecimal discountAmount;
    BigDecimal discountPercent;
    BigDecimal minOrderValue;
    BigDecimal maxDiscountAmount;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Integer usageLimit;
    Integer usedCount;

    @OneToMany(mappedBy = "platformVoucher")
    Set<Order> ordersWithPlatformVoucher = new HashSet<>();

    @OneToMany(mappedBy = "shopVoucher")
    Set<OrderItem> orderItemsWithShopVoucher = new HashSet<>();

    @CreationTimestamp
    LocalDateTime createdAt;
}