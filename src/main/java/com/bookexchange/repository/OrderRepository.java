package com.bookexchange.repository;

import com.bookexchange.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    
    long countByStatus(int status);
    
    @Query(value = "SELECT SUM(total_price) FROM orders WHERE status = :status", nativeQuery = true)
    Long getTotalRevenue(@Param("status") int status);
    

    @Query(value = "SELECT DATE_FORMAT(created_at, '%Y-%m-%d') as date, SUM(total_price) as revenue " +
            "FROM orders " +
            "WHERE created_at BETWEEN :startDate AND :endDate " +
            "AND status >= :status " +
            "GROUP BY DATE_FORMAT(created_at, '%Y-%m-%d') " +
            "ORDER BY date", nativeQuery = true)
    List<Object[]> getRevenueByDay(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") int status);
    

    @Query(value = "SELECT CONCAT(YEAR(created_at), '-', WEEK(created_at)) as week, SUM(total_price) as revenue " +
            "FROM orders " +
            "WHERE created_at BETWEEN :startDate AND :endDate " +
            "AND status >= :status " +
            "GROUP BY YEAR(created_at), WEEK(created_at), CONCAT(YEAR(created_at), '-', WEEK(created_at)) " +
            "ORDER BY YEAR(created_at), WEEK(created_at)", nativeQuery = true)
    List<Object[]> getRevenueByWeek(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") int status);
            

    @Query(value = "SELECT CONCAT(YEAR(created_at), '-', MONTH(created_at)) as month, SUM(total_price) as revenue " +
            "FROM orders " +
            "WHERE created_at BETWEEN :startDate AND :endDate " +
            "AND status >= :status " +
            "GROUP BY YEAR(created_at), MONTH(created_at), CONCAT(YEAR(created_at), '-', MONTH(created_at)) " +
            "ORDER BY YEAR(created_at), MONTH(created_at)", nativeQuery = true)
    List<Object[]> getRevenueByMonth(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") int status);
            
 
    @Query(value = "SELECT YEAR(created_at) as year, SUM(total_price) as revenue " +
            "FROM orders " +
            "WHERE created_at BETWEEN :startDate AND :endDate " +
            "AND status >= :status " +
            "GROUP BY YEAR(created_at) " +
            "ORDER BY YEAR(created_at)", nativeQuery = true)
    List<Object[]> getRevenueByYear(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") int status);
}