package com.bookexchange.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    String description;

    BigDecimal discountAmount;

    @Column(name = "discount_percentage")
    Integer discountPercentage;

    @Column(name = "min_order_value")
    BigDecimal minOrderValue;

    @Column(name = "max_discount")
    BigDecimal maxDiscount;

    @Column(name = "starts_at")
    LocalDateTime startsAt;

    @Column(name = "expires_at")
    LocalDateTime expiresAt;

    @Column(name = "max_uses")
    Integer maxUses;

    @Column(name = "current_uses")
    Integer currentUses;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    User seller;

    @OneToMany(mappedBy = "platformVoucher")
    Set<Order> ordersWithPlatformVoucher = new HashSet<>();
}