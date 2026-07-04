package com.example.graphqldemo.repository;

import com.example.graphqldemo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    Optional<Customer> findByEmail(String email);
    
    List<Customer> findByNameContainingIgnoreCase(String name);
    
    // Custom query to fetch customer with all related data (4-table join)
    @Query("""
        SELECT DISTINCT c FROM Customer c
        LEFT JOIN FETCH c.orders o
        LEFT JOIN FETCH o.orderItems oi
        LEFT JOIN FETCH oi.product p
        WHERE c.id = :customerId
        """)
    Optional<Customer> findCustomerWithCompleteDetails(@Param("customerId") Long customerId);
    
    // Query to get customers with their order count
    @Query("""
        SELECT c FROM Customer c
        LEFT JOIN FETCH c.orders
        """)
    List<Customer> findAllWithOrders();
}

// Made with Bob
