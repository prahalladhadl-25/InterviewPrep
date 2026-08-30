# Dependency Inversion Principle (DIP)

> **"D"** in **SOLID**

---

## Definition

> **High-level modules should NOT depend on low-level modules. Both should depend on ABSTRACTIONS.**
> **Abstractions should NOT depend on details. Details should depend on ABSTRACTIONS.**

In plain English: **Program to interfaces, not to implementations.**

---

## The Problem Without DIP

**Use Case: Order Processing System**

`OrderService` directly instantiates `EmailNotifier` — tightly coupled.

```java
// Low-level module
public class EmailNotifier {
    public void sendEmail(String message) {
        System.out.println("Sending EMAIL: " + message);
    }
}

// High-level module -- DIRECTLY depends on low-level (WRONG)
public class OrderService {
    private EmailNotifier notifier = new EmailNotifier(); // hard dependency!

    public void processOrder(Order order) {
        // ... process logic
        notifier.sendEmail("Order placed: " + order.getId());
    }
}
```

### Problems

| Problem | Impact |
|---|---|
| `OrderService` hardwired to `EmailNotifier` | Cannot switch to SMS/Push without changing `OrderService` |
| Tight coupling | Unit testing requires a real `EmailNotifier` |
| Violates Open/Closed too | Every new notification type needs core service changes |

---

## The Fix -- Apply DIP

### Step 1: Define the Abstraction (Interface)

```java
// Both high & low level modules depend on THIS
public interface NotificationService {
    void notify(String message);
}
```

### Step 2: Low-level modules implement the abstraction

```java
public class EmailNotifier implements NotificationService {
    @Override
    public void notify(String message) {
        System.out.println("EMAIL: " + message);
    }
}

public class SMSNotifier implements NotificationService {
    @Override
    public void notify(String message) {
        System.out.println("SMS: " + message);
    }
}

public class PushNotifier implements NotificationService {
    @Override
    public void notify(String message) {
        System.out.println("PUSH: " + message);
    }
}
```

### Step 3: High-level module depends on the abstraction

```java
public class OrderService {

    private final NotificationService notifier; // interface, not concrete!

    // Dependency INJECTED from outside (constructor injection)
    public OrderService(NotificationService notifier) {
        this.notifier = notifier;
    }

    public void processOrder(Order order) {
        // ... business logic
        notifier.notify("Order placed: " + order.getId());
    }
}
```

### Step 4: Wiring -- Spring Boot or plain main()

```java
// Spring Boot -- Spring resolves the correct bean automatically
@Service
public class OrderService {
    private final NotificationService notifier;

    @Autowired
    public OrderService(NotificationService notifier) {
        this.notifier = notifier;
    }
}

@Component
public class EmailNotifier implements NotificationService { ... }

// -- OR -- plain main()
public class Main {
    public static void main(String[] args) {
        NotificationService notifier = new SMSNotifier(); // swap anytime!
        OrderService service = new OrderService(notifier);
        service.processOrder(new Order("ORD-101"));
    }
}
```

---

## Unit Testing Becomes Trivial

```java
@Test
public void testOrderService() {
    // Mock the abstraction -- no real email/SMS sent!
    NotificationService mockNotifier = Mockito.mock(NotificationService.class);
    OrderService service = new OrderService(mockNotifier);

    service.processOrder(new Order("ORD-001"));

    Mockito.verify(mockNotifier).notify("Order placed: ORD-001");
}
```

No need to set up SMTP servers or SMS gateways in tests.

---

## Before vs After

| Before DIP | After DIP |
|---|---|
| `OrderService` creates `new EmailNotifier()` | Dependency injected via constructor |
| Hardwired to one notification type | Can swap Email/SMS/Push at runtime |
| Hard to unit test | Easily mockable with Mockito |
| Adding new notifier = modifying core service | Just add a new class implementing the interface |

---

## Key Insight

> Think of a **power socket** (abstraction). Your laptop, phone, and lamp all plug into the same socket
> without the wall caring what is plugged in. The socket is the interface -- devices are the implementations.

This is also the foundation of how **Spring's IoC container** and **dependency injection** work under the hood.
