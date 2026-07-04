# GraphQL Spring Boot Demo - 4-Table Join Example

A complete Spring Boot application demonstrating GraphQL API with data fetching from 4 joined SQL tables.

## 📋 Overview

This project showcases:
- **GraphQL API** using Spring for GraphQL
- **4-Table Join Queries**: Customer → Order → OrderItem → Product
- **H2 In-Memory Database** with sample data
- **JPA/Hibernate** for ORM
- **Optimized Queries** with JOIN FETCH to avoid N+1 problem

## 🏗️ Database Schema

```
customers (Table 1)
    ↓ (1:N)
orders (Table 2)
    ↓ (1:N)
order_items (Table 3)
    ↓ (N:1)
products (Table 4)
```

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Run the Application

```bash
# Clone or navigate to project directory
cd GraphQL

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 🔍 Access Points

### GraphiQL Interface (Recommended)
Open your browser and navigate to:
```
http://localhost:8080/graphiql
```

### GraphQL Endpoint
```
POST http://localhost:8080/graphql
```

### H2 Database Console
```
http://localhost:8080/h2-console

JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (leave empty)
```

## 📝 Sample GraphQL Queries

### 1. Get Customer with Complete Details (4-Table Join)

This query fetches data from all 4 tables in a single request:

```graphql
query {
  customerWithCompleteDetails(id: 1) {
    id
    name
    email
    phone
    address
    orders {
      id
      orderDate
      status
      totalAmount
      orderItems {
        id
        quantity
        unitPrice
        subtotal
        product {
          id
          name
          price
          category
        }
      }
    }
  }
}
```

### 2. Get Order with Complete Details (4-Table Join)

```graphql
query {
  orderWithCompleteDetails(id: 1) {
    id
    orderDate
    status
    totalAmount
    customer {
      id
      name
      email
    }
    orderItems {
      id
      quantity
      unitPrice
      subtotal
      product {
        id
        name
        description
        price
        category
      }
    }
  }
}
```

### 3. Get All Customers with Orders

```graphql
query {
  customers {
    id
    name
    email
    orders {
      id
      status
      totalAmount
      orderDate
    }
  }
}
```

### 4. Get Orders by Customer

```graphql
query {
  ordersByCustomer(customerId: 2) {
    id
    orderDate
    status
    totalAmount
    orderItems {
      quantity
      product {
        name
        price
      }
    }
  }
}
```

### 5. Get Orders by Status

```graphql
query {
  ordersByStatus(status: "DELIVERED") {
    id
    orderDate
    totalAmount
    customer {
      name
      email
    }
    orderItems {
      quantity
      product {
        name
      }
    }
  }
}
```

### 6. Get Products by Category

```graphql
query {
  productsByCategory(category: "Electronics") {
    id
    name
    description
    price
    stockQuantity
  }
}
```

### 7. Get All Products

```graphql
query {
  products {
    id
    name
    price
    category
    stockQuantity
  }
}
```

## 🗂️ Project Structure

```
GraphQL/
├── src/
│   ├── main/
│   │   ├── java/com/example/graphqldemo/
│   │   │   ├── controller/
│   │   │   │   └── GraphQLController.java      # GraphQL resolvers
│   │   │   ├── entity/
│   │   │   │   ├── Customer.java                # Customer entity
│   │   │   │   ├── Order.java                   # Order entity
│   │   │   │   ├── OrderItem.java               # OrderItem entity
│   │   │   │   └── Product.java                 # Product entity
│   │   │   ├── repository/
│   │   │   │   ├── CustomerRepository.java      # Customer data access
│   │   │   │   ├── OrderRepository.java         # Order data access
│   │   │   │   ├── OrderItemRepository.java     # OrderItem data access
│   │   │   │   └── ProductRepository.java       # Product data access
│   │   │   ├── service/
│   │   │   │   └── OrderService.java            # Business logic
│   │   │   └── GraphqlDemoApplication.java      # Main application
│   │   └── resources/
│   │       ├── graphql/
│   │       │   └── schema.graphqls              # GraphQL schema
│   │       ├── application.yml                  # Application config
│   │       ├── schema.sql                       # Database schema
│   │       └── data.sql                         # Sample data
│   └── test/
└── pom.xml                                      # Maven dependencies
```

## 🔧 Key Features

### 1. Optimized 4-Table Join Queries

The application uses `JOIN FETCH` to efficiently load related data:

```java
@Query("""
    SELECT DISTINCT c FROM Customer c
    LEFT JOIN FETCH c.orders o
    LEFT JOIN FETCH o.orderItems oi
    LEFT JOIN FETCH oi.product p
    WHERE c.id = :customerId
    """)
Optional<Customer> findCustomerWithCompleteDetails(@Param("customerId") Long customerId);
```

### 2. Avoiding N+1 Problem

By using `JOIN FETCH`, we avoid the N+1 query problem common in GraphQL APIs.

### 3. Flexible Queries

GraphQL allows clients to request exactly the data they need:
- Request only customer names
- Request full order details with products
- Request specific fields from any table

## 📊 Sample Data

The application comes pre-loaded with:
- **5 Customers**
- **10 Products** (Electronics, Furniture, Stationery)
- **8 Orders** with various statuses
- **Multiple Order Items** linking orders to products

## 🛠️ Technologies Used

- **Spring Boot 3.2.0**
- **Spring for GraphQL**
- **Spring Data JPA**
- **H2 Database** (in-memory)
- **Lombok** (reduces boilerplate)
- **Maven**

## 📈 Performance Considerations

1. **JOIN FETCH**: Reduces database round trips
2. **Lazy Loading**: Related entities loaded only when needed
3. **Indexed Foreign Keys**: Faster join operations
4. **Connection Pooling**: Efficient database connections

## 🔄 REST vs GraphQL Comparison

### REST Approach (Multiple Requests)
```
GET /api/customers/1
GET /api/customers/1/orders
GET /api/orders/1/items
GET /api/products/1
```

### GraphQL Approach (Single Request)
```graphql
query {
  customer(id: 1) {
    name
    orders {
      orderItems {
        product {
          name
        }
      }
    }
  }
}
```

## 🧪 Testing

You can test the API using:
1. **GraphiQL** (built-in): `http://localhost:8080/graphiql`
2. **Postman**: Send POST requests to `http://localhost:8080/graphql`
3. **cURL**:
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"{ customers { id name email } }"}'
```

## 📚 Learning Resources

- [Spring for GraphQL Documentation](https://docs.spring.io/spring-graphql/docs/current/reference/html/)
- [GraphQL Official Documentation](https://graphql.org/learn/)
- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)

## 🤝 Contributing

Feel free to fork, modify, and use this project for learning purposes.

## 📄 License

This project is open source and available for educational purposes.

---

**Happy Coding! 🚀**