# Java 8 Stream Operations — Complete Reference Table

---

## Intermediate Operations
> 🔁 **Lazy** — do not execute until a terminal operation is called. Returns a `Stream`.

| Operation | Method | Use When | Example |
|---|---|---|---|
| **Filter** | `filter(Predicate)` | Remove unwanted elements | `filter(e -> e.getSalary() > 50000)` |
| **Map** | `map(Function)` | Transform each element | `map(Employee::getName)` |
| **FlatMap** | `flatMap(Function)` | Flatten nested collections | `flatMap(List::stream)` |
| **Distinct** | `distinct()` | Remove duplicates | `distinct()` |
| **Sorted** | `sorted()` | Natural order sort | `sorted()` |
| **Sorted** | `sorted(Comparator)` | Custom order sort | `sorted(Comparator.reverseOrder())` |
| **Peek** | `peek(Consumer)` | Debug/log without changing stream | `peek(e -> log.info(e.getName()))` |
| **Limit** | `limit(n)` | Take first N elements | `limit(5)` |
| **Skip** | `skip(n)` | Skip first N elements | `skip(2)` |
| **MapToInt** | `mapToInt(Function)` | Convert to IntStream | `mapToInt(Employee::getAge)` |
| **MapToDouble** | `mapToDouble(Function)` | Convert to DoubleStream | `mapToDouble(Employee::getSalary)` |
| **MapToLong** | `mapToLong(Function)` | Convert to LongStream | `mapToLong(Employee::getId)` |

---

## Terminal Operations
> ⚡ **Eager** — triggers the entire pipeline. Returns a result, not a Stream.

| Operation | Method | Use When | Example |
|---|---|---|---|
| **Collect** | `collect(Collector)` | Gather into List/Set/Map | `collect(Collectors.toList())` |
| **forEach** | `forEach(Consumer)` | Iterate / print each element | `forEach(System.out::println)` |
| **count** | `count()` | Count elements | `count()` |
| **findFirst** | `findFirst()` | Get first matching element | `findFirst().orElse(null)` |
| **findAny** | `findAny()` | Get any element (good for parallel) | `findAny().orElse(null)` |
| **anyMatch** | `anyMatch(Predicate)` | Check if ANY element matches | `anyMatch(e -> e.getSalary() > 100000)` |
| **allMatch** | `allMatch(Predicate)` | Check if ALL elements match | `allMatch(e -> e.getAge() > 18)` |
| **noneMatch** | `noneMatch(Predicate)` | Check if NO element matches | `noneMatch(e -> e.getSalary() < 0)` |
| **min** | `min(Comparator)` | Find minimum element | `min(Comparator.comparingDouble(Employee::getSalary))` |
| **max** | `max(Comparator)` | Find maximum element | `max(Comparator.comparingDouble(Employee::getSalary))` |
| **reduce** | `reduce(BinaryOperator)` | Aggregate to single value | `reduce(0, Integer::sum)` |
| **toArray** | `toArray()` | Convert stream to array | `toArray(String[]::new)` |

---

## Collectors — `collect()` Variants

| Collector | Use When | Example |
|---|---|---|
| `toList()` | Collect into a List | `collect(Collectors.toList())` |
| `toSet()` | Collect into a Set (no duplicates) | `collect(Collectors.toSet())` |
| `toMap()` | Collect into a Map | `collect(toMap(Employee::getId, Employee::getName))` |
| `groupingBy()` | Group elements by a key | `collect(groupingBy(Employee::getDepartment))` |
| `counting()` | Count elements per group | `collect(groupingBy(dept, counting()))` |
| `averagingDouble()` | Average per group | `collect(groupingBy(dept, averagingDouble(salary)))` |
| `summingDouble()` | Sum per group | `collect(groupingBy(dept, summingDouble(salary)))` |
| `joining()` | Concatenate strings | `collect(joining(", "))` |
| `partitioningBy()` | Split into true/false groups | `collect(partitioningBy(e -> e.getSalary() > 50000))` |
| `toUnmodifiableList()` | Immutable list | `collect(Collectors.toUnmodifiableList())` |

---

## Numeric Stream Operations (IntStream / DoubleStream)

| Operation | Use When | Example |
|---|---|---|
| `sum()` | Total of all elements | `mapToDouble(getSalary).sum()` |
| `average()` | Mean of all elements | `mapToDouble(getSalary).average()` |
| `min()` | Minimum value | `mapToInt(getAge).min()` |
| `max()` | Maximum value | `mapToInt(getAge).max()` |
| `summaryStatistics()` | Get all stats at once | `mapToDouble(getSalary).summaryStatistics()` |
| `range(start, end)` | Generate range (exclusive end) | `IntStream.range(0, 10)` |
| `rangeClosed(start, end)` | Generate range (inclusive end) | `IntStream.rangeClosed(1, 10)` |

---

## One-Line Cheat Sheet

```java
stream()                        // create stream from collection
  .filter(predicate)            // keep matching elements
  .map(function)                // transform elements
  .flatMap(function)            // flatten nested collections
  .distinct()                   // remove duplicates
  .sorted(comparator)           // sort elements
  .peek(consumer)               // debug without affecting stream
  .limit(n)                     // take first N
  .skip(n)                      // skip first N
  .collect(Collectors.toList()) // gather to list       → TERMINAL
  .forEach(consumer)            // print/consume        → TERMINAL
  .count()                      // count                → TERMINAL
  .findFirst()                  // first element        → TERMINAL
  .anyMatch(predicate)          // any match?           → TERMINAL
  .reduce(accumulator)          // aggregate            → TERMINAL
  .min/max(comparator)          // min or max           → TERMINAL
```

---

> 💡 **Golden Rule**:
> - **Intermediate** operations = **lazy**, chainable, return `Stream`
> - **Terminal** operations = **eager**, trigger pipeline, return result
> - A stream pipeline **must end** with exactly **one terminal operation**
