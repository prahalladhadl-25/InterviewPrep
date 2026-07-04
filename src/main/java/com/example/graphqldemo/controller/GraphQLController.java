package com.example.graphqldemo.controller;

import com.example.graphqldemo.entity.Customer;
import com.example.graphqldemo.entity.Order;
import com.example.graphqldemo.entity.Product;
import com.example.graphqldemo.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL Controller for handling queries
 * This demonstrates fetching data from 4 joined tables using GraphQL
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class GraphQLController {
    
    private final OrderService orderService;
    
    // ==================== Customer Queries ====================
    
    @QueryMapping
    public List<Customer> customers() {
        log.info("GraphQL Query: customers");
        return orderService.getAllCustomersWithOrders();
    }
    
    @QueryMapping
    public Customer customer(@Argument Long id) {
        log.info("GraphQL Query: customer(id: {})", id);
        return orderService.getCustomerWithCompleteDetails(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }
    
    /**
     * This query demonstrates fetching data from all 4 tables:
     * Customer -> Orders -> OrderItems -> Products
     */
    @QueryMapping
    public Customer customerWithCompleteDetails(@Argument Long id) {
        log.info("GraphQL Query: customerWithCompleteDetails(id: {})", id);
        return orderService.getCustomerWithCompleteDetails(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }
    
    // ==================== Order Queries ====================
    
    @QueryMapping
    public List<Order> orders() {
        log.info("GraphQL Query: orders");
        return orderService.getAllOrdersWithDetails();
    }
    
    @QueryMapping
    public Order order(@Argument Long id) {
        log.info("GraphQL Query: order(id: {})", id);
        return orderService.getOrderById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }
    
    /**
     * This query demonstrates fetching data from all 4 tables:
     * Order -> Customer, OrderItems -> Products
     */
    @QueryMapping
    public Order orderWithCompleteDetails(@Argument Long id) {
        log.info("GraphQL Query: orderWithCompleteDetails(id: {})", id);
        return orderService.getOrderWithCompleteDetails(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }
    
    @QueryMapping
    public List<Order> ordersByCustomer(@Argument Long customerId) {
        log.info("GraphQL Query: ordersByCustomer(customerId: {})", customerId);
        return orderService.getOrdersByCustomerIdWithDetails(customerId);
    }
    
    @QueryMapping
    public List<Order> ordersByStatus(@Argument String status) {
        log.info("GraphQL Query: ordersByStatus(status: {})", status);
        return orderService.getOrdersByStatus(status);
    }
    
    // ==================== Product Queries ====================
    
    @QueryMapping
    public List<Product> products() {
        log.info("GraphQL Query: products");
        return orderService.getAllProducts();
    }
    
    @QueryMapping
    public Product product(@Argument Long id) {
        log.info("GraphQL Query: product(id: {})", id);
        return orderService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }
    
    @QueryMapping
    public List<Product> productsByCategory(@Argument String category) {
        log.info("GraphQL Query: productsByCategory(category: {})", category);
        return orderService.getProductsByCategory(category);
    }
}

// Made with Bob
