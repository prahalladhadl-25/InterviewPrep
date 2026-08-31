# Open/Closed Principle (OCP)

> **"O"** in **SOLID**

---

## Definition

> **"Software entities (classes, modules, functions) should be OPEN for extension but CLOSED for modification."**

Coined by Bertrand Meyer, popularised by Robert Martin.

In plain English: **You should be able to add new behaviour without touching existing, tested code.**

---

## Why It Matters in Real Projects

In my experience, the most dangerous thing you can do in a production codebase is go back and edit
a class that already works. Every time you touch it, you risk:
- Introducing regressions
- Breaking other code that depends on it
- Invalidating existing test coverage

OCP is the discipline that prevents that. You design your classes so that
**new requirements = new classes**, not modified old ones.

---

## The Problem Without OCP

**Use Case: Discount calculation engine for an e-commerce platform.**

New business requirement comes in every sprint -- seasonal discount, loyalty discount, flash sale...
and every time, a developer cracks open `DiscountService` and adds another if-else.

```java
// This class keeps growing with every new requirement -- violates OCP
public class DiscountService {

    public double applyDiscount(String customerType, double price) {
        if (customerType.equals("REGULAR")) {
            return price * 0.95;          // 5% off
        } else if (customerType.equals("PREMIUM")) {
            return price * 0.80;          // 20% off
        } else if (customerType.equals("EMPLOYEE")) {
            return price * 0.60;          // 40% off
        }
        // Next sprint: add SEASONAL, FLASH_SALE, LOYALTY...
        // This method NEVER stops growing
        return price;
    }
}
```

### Problems

| Problem | Impact |
|---|---|
| Add new discount type = modify `DiscountService` | Risk regression in existing discount logic |
| String-based branching | Typo in "PREMIUM" silently returns full price |
| Impossible to unit test each discount in isolation | One test class tests everything |
| Violates SRP too | One class owns ALL discount business rules |

---

## The Fix -- Apply OCP

Define an abstraction for discount behaviour. Each new discount type is a **new class**, not a new if-else.

### Step 1: Define the abstraction

```java
// The contract -- CLOSED for modification, OPEN for extension via new implementations
public interface DiscountStrategy {
    double apply(double price);
}
```

### Step 2: Each discount is its own class

```java
public class RegularDiscount implements DiscountStrategy {
    @Override
    public double apply(double price) {
        return price * 0.95; // 5% off
    }
}

public class PremiumDiscount implements DiscountStrategy {
    @Override
    public double apply(double price) {
        return price * 0.80; // 20% off
    }
}

public class EmployeeDiscount implements DiscountStrategy {
    @Override
    public double apply(double price) {
        return price * 0.60; // 40% off
    }
}

// New requirement next sprint? Just add a NEW class -- touch NOTHING existing
public class SeasonalDiscount implements DiscountStrategy {
    @Override
    public double apply(double price) {
        return price * 0.70; // 30% off
    }
}

public class FlashSaleDiscount implements DiscountStrategy {
    @Override
    public double apply(double price) {
        return price * 0.50; // 50% off
    }
}
```

### Step 3: DiscountService is now closed for modification

```java
public class DiscountService {

    private final DiscountStrategy strategy;

    // Inject the strategy -- follows DIP too
    public DiscountService(DiscountStrategy strategy) {
        this.strategy = strategy;
    }

    public double getFinalPrice(double price) {
        return strategy.apply(price); // delegates -- never changes
    }
}
```

### Step 4: Wiring in Spring Boot

```java
@Service
public class OrderFacade {

    private final Map<String, DiscountStrategy> strategies;

    public OrderFacade(List<DiscountStrategy> strategyList) {
        this.strategies = strategyList.stream()
            .collect(Collectors.toMap(
                s -> s.getClass().getSimpleName(),
                s -> s
            ));
    }

    public double checkout(String customerType, double price) {
        DiscountStrategy strategy = strategies
            .getOrDefault(customerType, p -> p); // no discount as default
        return new DiscountService(strategy).getFinalPrice(price);
    }
}
```

---

## Unit Testing -- Each Discount Tested in Total Isolation

```java
@Test
public void testPremiumDiscount() {
    DiscountStrategy strategy = new PremiumDiscount();
    double result = strategy.apply(100.0);
    assertEquals(80.0, result, 0.001); // focused, zero noise
}

@Test
public void testFlashSaleDiscount() {
    DiscountStrategy strategy = new FlashSaleDiscount();
    double result = strategy.apply(200.0);
    assertEquals(100.0, result, 0.001); // new class, new test, old tests untouched
}

@Test
public void testDiscountService_delegatesToStrategy() {
    DiscountStrategy mockStrategy = Mockito.mock(DiscountStrategy.class);
    Mockito.when(mockStrategy.apply(100.0)).thenReturn(75.0);

    DiscountService service = new DiscountService(mockStrategy);

    assertEquals(75.0, service.getFinalPrice(100.0), 0.001);
    // DiscountService itself has ZERO business logic to test
}
```

---

## Real-World Pattern -- This IS the Strategy Pattern

OCP in practice almost always maps to one of these GoF patterns:

| Pattern | How it implements OCP |
|---|---|
| **Strategy** | Swap algorithms without touching the caller (`DiscountStrategy` above) |
| **Template Method** | Define skeleton in base class, subclasses fill in the steps |
| **Decorator** | Wrap existing behaviour with new behaviour without modifying the original |
| **Chain of Responsibility** | Add new handlers without modifying the chain |

OCP is not just a principle -- it is the motivation behind the entire Strategy and Template Method pattern family.

---

## Before vs After

| Before OCP | After OCP |
|---|---|
| if-else chain grows with every sprint | New discount = new class, zero edits to existing code |
| Testing one discount risks breaking another | Each strategy has its own isolated unit test |
| String-based type matching | Type-safe polymorphism |
| `DiscountService` owns all business rules | `DiscountService` owns zero business rules -- just delegates |
| High regression risk on every change | Existing tests always green -- new tests added alongside new classes |

---

## Key Insight

> In 10 years of Java development, the two places OCP is violated most are:
>
> 1. **Report generators** -- one class with `if (type == "PDF") ... else if (type == "EXCEL") ...`
> 2. **Notification systems** -- one class that handles Email, SMS, Push with if-else blocks
>
> Both are fixed the same way: extract an interface, push each behaviour into its own class,
> inject the right one. The original class never changes again -- that is OCP at work.
