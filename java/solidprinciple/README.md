# SOLID Principles — Java Interview Reference

> A complete reference for the **5 SOLID principles** with real-world Java use cases,
> written from the perspective of a 10-year Java developer preparing for senior interviews.

---

## What is SOLID?

**SOLID** is an acronym for five object-oriented design principles introduced by Robert C. Martin
(Uncle Bob). Together they guide you toward writing code that is:

- Easy to **extend** without breaking existing behaviour
- Easy to **test** in isolation
- Easy to **maintain** without cascading changes
- Resilient to **requirement changes** over time

| Letter | Principle | One-line Definition |
|---|---|---|
| **S** | [Single Responsibility](#s--single-responsibility-principle-srp) | A class should have only ONE reason to change |
| **O** | [Open/Closed](#o--openclosed-principle-ocp) | Open for extension, closed for modification |
| **L** | [Liskov Substitution](#l--liskov-substitution-principle-lsp) | Subtypes must be substitutable for their base types |
| **I** | [Interface Segregation](#i--interface-segregation-principle-isp) | No client should be forced to implement interfaces it does not use |
| **D** | [Dependency Inversion](#d--dependency-inversion-principle-dip) | Depend on abstractions, not on concretions |

---

## S — Single Responsibility Principle (SRP)

> **"A class should have only ONE reason to change."**

**Signal of violation:** You describe a class using the word **"and"** —
*"This class validates users **and** saves to DB **and** sends emails."*

**Use Case:** User Registration System

| Bad (Violates SRP) | Good (Follows SRP) |
|---|---|
| `UserService` does validation + hashing + DB save + email + audit log | `UserValidator`, `PasswordEncoder`, `UserRepository`, `EmailService`, `AuditLogger` — one job each |
| 1 class, 200+ lines, 5 reasons to change | 5 classes, each under 30 lines, 1 reason each |
| 3 developers conflict on same file | Each team owns their own class |

**Rule of thumb:** If changing a marketing email template forces you to touch the same class
as a DB migration, SRP is violated.

📄 [Full explanation with code →](./SingleResponsibilityPrinciple.md)

---

## O — Open/Closed Principle (OCP)

> **"Software entities should be OPEN for extension but CLOSED for modification."**

**Signal of violation:** A growing `if-else` or `switch` block that gains a new branch every sprint.

**Use Case:** Discount Calculation Engine

| Bad (Violates OCP) | Good (Follows OCP) |
|---|---|
| `DiscountService` has `if REGULAR... else if PREMIUM... else if EMPLOYEE...` | `DiscountStrategy` interface + `RegularDiscount`, `PremiumDiscount`, `EmployeeDiscount` classes |
| New discount type = edit existing class = risk regression | New discount type = new class, zero edits to existing code |

**Rule of thumb:** If adding a new feature requires modifying a class that already works and is
already tested, OCP is violated.

📄 [Full explanation with code →](./OpenClosedPrinciple.md)

---

## L — Liskov Substitution Principle (LSP)

> **"Objects of a subclass should be replaceable with objects of the superclass without breaking the program."**

**Signal of violation:** A subclass throws `UnsupportedOperationException`, has empty overrides,
or forces callers to use `instanceof` checks.

**Use Cases:** Rectangle/Square problem + Payment Processing System

| Bad (Violates LSP) | Good (Follows LSP) |
|---|---|
| `Square extends Rectangle` — `setHeight(10)` also changes width, breaks area calculation | Separate `Shape` hierarchy where no subtype changes parent contract |
| `CashPayment extends OnlinePayment` — `refund()` throws `UnsupportedOperationException` | `CashPayment extends Payment` (no refund); `OnlinePayment implements Refundable` |

**Rule of thumb:** If you can't hand a subtype to a method expecting the parent without
reading the subtype's source code first, LSP is violated.

📄 [Full explanation with code →](./LiskovSubstitutionPrinciple.md)

---

## I — Interface Segregation Principle (ISP)

> **"A client should never be forced to implement an interface it does not use."**

**Signal of violation:** A class implements an interface but throws `UnsupportedOperationException`
or leaves methods empty for several of its methods.

**Use Case:** Workplace Management System

| Bad (Violates ISP) | Good (Follows ISP) |
|---|---|
| Fat `Worker` interface: `work()`, `eat()`, `attendMeeting()`, `writeCode()`, `manageTeam()` | `Workable`, `Eatable`, `Meetable`, `Codeable`, `Manageable` — five lean interfaces |
| `RobotWorker` forced to implement `eat()` and `attendMeeting()` | `RobotWorker implements Workable, Codeable` — only what it needs |

**Rule of thumb:** If a mock in your test needs to stub methods you don't care about,
your interface is too fat — ISP is being violated.

📄 [Full explanation with code →](./InterfaceSegregationPrinciple.md)

---

## D — Dependency Inversion Principle (DIP)

> **"High-level modules should not depend on low-level modules. Both should depend on abstractions."**
> **"Abstractions should not depend on details. Details should depend on abstractions."**

**Signal of violation:** A high-level class uses `new ConcreteClass()` inside itself to create
its own dependencies.

**Use Case:** Order Notification System

| Bad (Violates DIP) | Good (Follows DIP) |
|---|---|
| `OrderService` has `private EmailNotifier notifier = new EmailNotifier()` | `OrderService` depends on `NotificationService` interface, injected via constructor |
| Hardwired to Email — can never switch to SMS without editing `OrderService` | Swap `EmailNotifier` for `SMSNotifier` or `PushNotifier` at runtime with zero code change |

**Rule of thumb:** If you cannot test a class without instantiating all of its dependencies for real
(no mocking), DIP is violated.

📄 [Full explanation with code →](./DependencyInversionPrinciple.md)

---

## How the Principles Connect

```
SRP  -->  Each class has one job
           |
           v
OCP  -->  New jobs = new classes, never edit old ones
           |
           v
LSP  -->  New classes honour the contract of their parent
           |
           v
ISP  -->  Contracts are lean so they are easy to honour
           |
           v
DIP  -->  Lean contracts are injected so high-level code never cares which impl it gets
```

> Violating one principle almost always drags in a violation of another.
> The `Square`/`Rectangle` LSP violation is fixed by ISP (split the interface).
> The fat `Worker` ISP violation leads to LSP problems (empty overrides).
> The tight coupling in DIP violations makes OCP impossible (you can't extend without modifying).
> They are five facets of the same underlying idea: **keep concerns small, separated, and composable.**

---

## Quick Cheat Sheet

| Principle | Bad Smell | Fix |
|---|---|---|
| SRP | Class described with "and" | Split into focused collaborator classes |
| OCP | `if-else` / `switch` grows every sprint | Extract interface + strategy classes |
| LSP | `UnsupportedOperationException` / empty overrides / `instanceof` checks | Redesign hierarchy, use ISP to split interfaces |
| ISP | Implementing methods just to throw or leave empty | Split fat interface into role-specific interfaces |
| DIP | `new ConcreteClass()` inside a high-level class | Inject via constructor + depend on interface |
