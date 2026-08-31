# Liskov Substitution Principle (LSP)

> **"L"** in **SOLID**

---

## Definition

> **"Objects of a subclass should be replaceable with objects of the superclass without breaking the program."**

Coined by Barbara Liskov in 1987.

In plain English: **If class B extends class A, you should be able to use B anywhere A is expected
and the program should still behave correctly.**

---

## Classic Violation -- The Rectangle / Square Problem

This is the most famous LSP violation asked in interviews.

```java
public class Rectangle {
    protected int width;
    protected int height;

    public void setWidth(int width)   { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public int getArea()              { return width * height; }
}

// Square extends Rectangle -- looks logical in real life, but WRONG in code
public class Square extends Rectangle {
    @Override
    public void setWidth(int width) {
        this.width  = width;  // a square must have equal sides
        this.height = width;  // so we override BOTH
    }

    @Override
    public void setHeight(int height) {
        this.width  = height;
        this.height = height;
    }
}
```

### Where it blows up

```java
public class AreaCalculator {

    // This method expects any Rectangle to behave like a Rectangle
    public void printArea(Rectangle r) {
        r.setWidth(5);
        r.setHeight(10);
        // Expected area = 5 * 10 = 50
        System.out.println("Area: " + r.getArea());
    }
}

public class Main {
    public static void main(String[] args) {
        AreaCalculator calc = new AreaCalculator();

        calc.printArea(new Rectangle()); // prints: Area: 50  -- correct
        calc.printArea(new Square());    // prints: Area: 100 -- WRONG!
        // Because setHeight(10) also set width=10, so 10*10=100
    }
}
```

### Why it violates LSP

```
Expected (Rectangle contract):  setWidth(5) then setHeight(10)  ->  area = 50
Actual   (Square behaviour):    setWidth(5) then setHeight(10)  ->  area = 100
```

`Square` **cannot** be substituted for `Rectangle` without breaking the program. LSP is violated.

---

## Real-World Use Case -- Payment Processing System

### The Violation First

```java
public class OnlinePayment {
    public void processPayment(double amount) {
        System.out.println("Processing online payment: $" + amount);
    }

    public void refund(double amount) {
        System.out.println("Refunding: $" + amount);
    }
}

// Cash payment extends OnlinePayment -- but cash can't be refunded digitally!
public class CashPayment extends OnlinePayment {
    @Override
    public void refund(double amount) {
        throw new UnsupportedOperationException("Cash payments cannot be refunded online!");
    }
}

public class PaymentProcessor {
    public void processAndRefund(OnlinePayment payment, double amount) {
        payment.processPayment(amount);
        payment.refund(amount); // BOOM when CashPayment is passed
    }
}
```

---

## The Fix -- Proper Abstraction Hierarchy

```java
// Base abstraction -- ALL payments can be processed
public abstract class Payment {
    public abstract void processPayment(double amount);
}

// Only some payments support refund -- use ISP here too!
public interface Refundable {
    void refund(double amount);
}

// Online payment -- supports both
public class OnlinePayment extends Payment implements Refundable {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing online payment: $" + amount);
    }

    @Override
    public void refund(double amount) {
        System.out.println("Refunding online: $" + amount);
    }
}

// Wallet payment -- supports both
public class WalletPayment extends Payment implements Refundable {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing wallet payment: $" + amount);
    }

    @Override
    public void refund(double amount) {
        System.out.println("Refunding to wallet: $" + amount);
    }
}

// Cash payment -- only processes, NO refund -- and that is fine!
public class CashPayment extends Payment {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing cash payment: $" + amount);
    }
    // No refund method at all -- no UnsupportedOperationException lurking
}
```

### Consumer code respects LSP

```java
public class PaymentProcessor {

    // Any Payment can be processed -- LSP holds for ALL subtypes
    public void process(Payment payment, double amount) {
        payment.processPayment(amount);
    }

    // Only called when the payment IS refundable -- no surprises
    public void refund(Refundable payment, double amount) {
        payment.refund(amount);
    }
}

public class Main {
    public static void main(String[] args) {
        PaymentProcessor processor = new PaymentProcessor();

        Payment online = new OnlinePayment();
        Payment wallet = new WalletPayment();
        Payment cash   = new CashPayment();

        // All three work perfectly -- substitutable
        processor.process(online, 100.0);
        processor.process(wallet, 200.0);
        processor.process(cash,   50.0);

        // Only refundable ones are passed here -- compile-time safety
        processor.refund((Refundable) online, 100.0);
        processor.refund((Refundable) wallet, 200.0);
        // processor.refund(cash, 50.0); // Won't even compile!
    }
}
```

---

## LSP Rules -- What Subtypes Must Honour

| Rule | Meaning |
|---|---|
| **Preconditions** cannot be strengthened | Subclass cannot demand MORE from the caller than the parent |
| **Postconditions** cannot be weakened | Subclass must deliver AT LEAST what the parent promised |
| **Invariants** must be preserved | Subclass must maintain the same constraints the parent guaranteed |
| **No new exceptions** | Subclass cannot throw exceptions the parent contract does not declare |

---

## Quick Smell Test for LSP Violations

```java
// Any of these patterns = LSP violation red flag

public void someMethod() {
    throw new UnsupportedOperationException(); // red flag
}

if (obj instanceof SubClass) {   // red flag -- you shouldn't need to type-check
    // do special handling
}

@Override
public void parentMethod() {
    // deliberately empty override  // red flag
}
```

---

## Before vs After

| Before LSP | After LSP |
|---|---|
| `Square` extends `Rectangle`, breaks area calculation | Separate `Shape` hierarchy with no shared mutable state |
| `CashPayment` throws `UnsupportedOperationException` | `CashPayment` only extends `Payment`, not `OnlinePayment` |
| Caller must type-check with `instanceof` | Caller trusts the contract -- no conditionals needed |
| Runtime surprises | Compile-time safety |

---

## Key Insight

> **LSP and ISP are deeply connected.**
> The `Square`/`Rectangle` problem and the `CashPayment`/`OnlinePayment` problem both get fixed
> by segregating the interface -- proving that violating LSP often signals you also need to apply ISP.

The three principles form a cycle:
- **LSP** ensures subtypes honour contracts
- **ISP** keeps those contracts lean so they are easy to honour
- **DIP** injects those contracts so high-level code never cares which subtype it gets
