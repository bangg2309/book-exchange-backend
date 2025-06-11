package com.bookexchange.repository;

import com.bookexchange.entity.OrderBookItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderBookItemRepository extends JpaRepository<OrderBookItem, Long> {
} 