package com.example.graphqldemo.service;

import com.example.graphqldemo.entity.Customer;
import com.example.graphqldemo.entity.Order;
import com.example.graphqldemo.entity.OrderItem;
import com.example.graphqldemo.entity.Product;
import com.example.graphqldemo.repository.CustomerRepository;
import com.example.graphqldemo.repository.OrderRepository;
import com.example.graphqldemo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    
    /**
     * Get all orders
     */
    public List<Order> getAllOrders() {
        log.info("Fetching all orders");
        return orderRepository.findAll();
    }
    
    /**
     * Get order by ID
     */
    public Optional<Order> getOrderById(Long id) {
        log.info("Fetching order with id: {}", id);
        return orderRepository.findById(id);
    }
    
    /**
     * Get order with complete details (4-table join)
     * This demonstrates fetching data from all 4 tables in a single query
     */
    public Optional<Order> getOrderWithCompleteDetails(Long orderId) {
        log.info("Fetching order with complete details for id: {}", orderId);
        return orderRepository.findOrderWithCompleteDetails(orderId);
    }
    
    /**
     * Get all orders with items and products
     */
    public List<Order> getAllOrdersWithDetails() {
        log.info("Fetching all orders with items and products");
        return orderRepository.findAllWithItemsAndProducts();
    }
    
    /**
     * Get orders by customer ID
     */
    public List<Order> getOrdersByCustomerId(Long customerId) {
        log.info("Fetching orders for customer id: {}", customerId);
        return orderRepository.findByCustomerId(customerId);
    }
    
    /**
     * Get orders by customer ID with complete details
     */
    public List<Order> getOrdersByCustomerIdWithDetails(Long customerId) {
        log.info("Fetching orders with details for customer id: {}", customerId);
        return orderRepository.findByCustomerIdWithDetails(customerId);
    }
    
    /**
     * Get orders by status
     */
    public List<Order> getOrdersByStatus(String status) {
        log.info("Fetching orders with status: {}", status);
        return orderRepository.findByStatus(status);
    }
    
    /**
     * Get customer with complete order details (4-table join)
     * This is another way to fetch data from all 4 tables
     */
    public Optional<Customer> getCustomerWithCompleteDetails(Long customerId) {
        log.info("Fetching customer with complete order details for id: {}", customerId);
        return customerRepository.findCustomerWithCompleteDetails(customerId);
    }
    
    /**
     * Get all customers with their orders
     */
    public List<Customer> getAllCustomersWithOrders() {
        log.info("Fetching all customers with orders");
        return customerRepository.findAllWithOrders();
    }
    
    /**
     * Get product by ID
     */
    public Optional<Product> getProductById(Long id) {
        log.info("Fetching product with id: {}", id);
        return productRepository.findById(id);
    }
    
    /**
     * Get all products
     */
    public List<Product> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll();
    }
    
    /**
     * Get products by category
     */
    public List<Product> getProductsByCategory(String category) {
        log.info("Fetching products in category: {}", category);
        return productRepository.findByCategory(category);
    }
}

// Made with Bob
