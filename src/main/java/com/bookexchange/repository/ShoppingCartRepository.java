package com.bookexchange.repository;

import com.bookexchange.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    Optional<ShoppingCart> findByUserId(long userId);

    @Query("SELECT sc FROM ShoppingCart sc LEFT JOIN FETCH sc.items WHERE sc.user.id = :userId")
    Optional<ShoppingCart> findByUserIdWithItems(@Param("userId") Long userId);

}