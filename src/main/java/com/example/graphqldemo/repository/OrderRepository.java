package com.example.graphqldemo.repository;

import com.example.graphqldemo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByCustomerId(Long customerId);
    
    List<Order> findByStatus(String status);
    
    // Fetch order with all related data (4-table join)
    @Query("""
        SELECT DISTINCT o FROM Order o
        LEFT JOIN FETCH o.customer c
        LEFT JOIN FETCH o.orderItems oi
        LEFT JOIN FETCH oi.product p
        WHERE o.id = :orderId
        """)
    Optional<Order> findOrderWithCompleteDetails(@Param("orderId") Long orderId);
    
    // Fetch all orders with items and products
    @Query("""
        SELECT DISTINCT o FROM Order o
        LEFT JOIN FETCH o.orderItems oi
        LEFT JOIN FETCH oi.product p
        """)
    List<Order> findAllWithItemsAndProducts();
    
    // Get orders by customer with all details
    @Query("""
        SELECT DISTINCT o FROM Order o
        LEFT JOIN FETCH o.customer c
        LEFT JOIN FETCH o.orderItems oi
        LEFT JOIN FETCH oi.product p
        WHERE c.id = :customerId
        """)
    List<Order> findByCustomerIdWithDetails(@Param("customerId") Long customerId);
}

// Made with Bob
