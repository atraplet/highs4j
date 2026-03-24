# Parameter Sealed Interface Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement HiGHS solver options as a sealed interface `Parameter` with four record permits (`BooleanParameter`, `IntParameter`, `DoubleParameter`, `StringParameter`).

**Architecture:** A sealed interface `Parameter` with `String name()` accessor, permitted by four top-level records — one per HiGHS option type. Each record validates its `name` is non-null/non-blank. No `Parameters` wrapper class; consumers use `List<Parameter>` directly.

**Tech Stack:** Java 25 (records, sealed interfaces, pattern matching), Lombok (`@NonNull`), Guava (`checkArgument`)

**Spec:** `docs/superpowers/specs/2026-03-24-parameter-sealed-interface-design.md`

---

## File Structure

| File | Action | Responsibility |
|------|--------|---------------|
| `src/main/java/com/ustermetrics/highs4j/Parameter.java` | Create | Sealed interface |
| `src/main/java/com/ustermetrics/highs4j/BooleanParameter.java` | Create | Boolean option record |
| `src/main/java/com/ustermetrics/highs4j/IntParameter.java` | Create | Integer option record |
| `src/main/java/com/ustermetrics/highs4j/DoubleParameter.java` | Create | Double option record |
| `src/main/java/com/ustermetrics/highs4j/StringParameter.java` | Create | String option record |
| `src/test/java/com/ustermetrics/highs4j/ParameterTest.java` | Create | Tests for all 5 types |

---

### Task 1: BooleanParameter (TDD)

**Files:**
- Create: `src/test/java/com/ustermetrics/highs4j/ParameterTest.java`
- Create: `src/main/java/com/ustermetrics/highs4j/Parameter.java`
- Create: `src/main/java/com/ustermetrics/highs4j/BooleanParameter.java`

- [ ] **Step 1: Write the failing tests for BooleanParameter**

Create the test file with tests for `BooleanParameter`:

```java
package com.ustermetrics.highs4j;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParameterTest {

    @Test
    void booleanParameterReturnsExpectedValues() {
        var p = new BooleanParameter("output_flag", true);

        assertEquals("output_flag", p.name());
        assertTrue(p.value());
    }

    @Test
    void booleanParameterIsParameter() {
        assertInstanceOf(Parameter.class, new BooleanParameter("output_flag", false));
    }

    @Test
    void booleanParameterThrowsOnNullName() {
        assertThrows(NullPointerException.class, () -> new BooleanParameter(null, true));
    }

    @Test
    void booleanParameterThrowsOnBlankName() {
        assertThrows(IllegalArgumentException.class, () -> new BooleanParameter(" ", true));
    }

}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `mvn test -Dtest=ParameterTest -pl .`
Expected: Compilation failure — `Parameter` and `BooleanParameter` do not exist.

- [ ] **Step 3: Write Parameter sealed interface and BooleanParameter record**

Create `src/main/java/com/ustermetrics/highs4j/Parameter.java`:

```java
package com.ustermetrics.highs4j;

public sealed interface Parameter permits BooleanParameter, IntParameter, DoubleParameter, StringParameter {

    String name();

}
```

Create `src/main/java/com/ustermetrics/highs4j/BooleanParameter.java`:

```java
package com.ustermetrics.highs4j;

import lombok.NonNull;

import static com.google.common.base.Preconditions.checkArgument;

public record BooleanParameter(@NonNull String name, boolean value) implements Parameter {

    public BooleanParameter {
        checkArgument(!name.isBlank());
    }

}
```

Note: The sealed interface permits all four records. `IntParameter`, `DoubleParameter`, and `StringParameter` don't exist yet, so compilation will fail until Task 2 stubs them or all four are created together. To keep TDD pure, create minimal stubs for the other three records now so the sealed interface compiles:

Create `src/main/java/com/ustermetrics/highs4j/IntParameter.java`:

```java
package com.ustermetrics.highs4j;

import lombok.NonNull;

import static com.google.common.base.Preconditions.checkArgument;

public record IntParameter(@NonNull String name, int value) implements Parameter {

    public IntParameter {
        checkArgument(!name.isBlank());
    }

}
```

Create `src/main/java/com/ustermetrics/highs4j/DoubleParameter.java`:

```java
package com.ustermetrics.highs4j;

import lombok.NonNull;

import static com.google.common.base.Preconditions.checkArgument;

public record DoubleParameter(@NonNull String name, double value) implements Parameter {

    public DoubleParameter {
        checkArgument(!name.isBlank());
    }

}
```

Create `src/main/java/com/ustermetrics/highs4j/StringParameter.java`:

```java
package com.ustermetrics.highs4j;

import lombok.NonNull;

import static com.google.common.base.Preconditions.checkArgument;

public record StringParameter(@NonNull String name, @NonNull String value) implements Parameter {

    public StringParameter {
        checkArgument(!name.isBlank());
    }

}
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `mvn test -Dtest=ParameterTest -pl .`
Expected: 4 tests PASS.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/ustermetrics/highs4j/Parameter.java \
       src/main/java/com/ustermetrics/highs4j/BooleanParameter.java \
       src/main/java/com/ustermetrics/highs4j/IntParameter.java \
       src/main/java/com/ustermetrics/highs4j/DoubleParameter.java \
       src/main/java/com/ustermetrics/highs4j/StringParameter.java \
       src/test/java/com/ustermetrics/highs4j/ParameterTest.java
git commit -m "Add Parameter sealed interface and BooleanParameter with tests"
```

---

### Task 2: IntParameter tests

**Files:**
- Modify: `src/test/java/com/ustermetrics/highs4j/ParameterTest.java`

`IntParameter` implementation already exists from Task 1 stubs. Add tests.

- [ ] **Step 1: Add failing tests for IntParameter**

Append to `ParameterTest.java`:

```java
@Test
void intParameterReturnsExpectedValues() {
    var p = new IntParameter("threads", 4);

    assertEquals("threads", p.name());
    assertEquals(4, p.value());
}

@Test
void intParameterIsParameter() {
    assertInstanceOf(Parameter.class, new IntParameter("threads", 0));
}

@Test
void intParameterThrowsOnNullName() {
    assertThrows(NullPointerException.class, () -> new IntParameter(null, 1));
}

@Test
void intParameterThrowsOnBlankName() {
    assertThrows(IllegalArgumentException.class, () -> new IntParameter("", 1));
}
```

- [ ] **Step 2: Run tests to verify they pass**

Run: `mvn test -Dtest=ParameterTest -pl .`
Expected: 8 tests PASS (implementation already exists from Task 1).

- [ ] **Step 3: Commit**

```bash
git add src/test/java/com/ustermetrics/highs4j/ParameterTest.java
git commit -m "Add IntParameter tests"
```

---

### Task 3: DoubleParameter tests

**Files:**
- Modify: `src/test/java/com/ustermetrics/highs4j/ParameterTest.java`

- [ ] **Step 1: Add failing tests for DoubleParameter**

Append to `ParameterTest.java`:

```java
@Test
void doubleParameterReturnsExpectedValues() {
    var p = new DoubleParameter("time_limit", 60.0);

    assertEquals("time_limit", p.name());
    assertEquals(60.0, p.value());
}

@Test
void doubleParameterIsParameter() {
    assertInstanceOf(Parameter.class, new DoubleParameter("time_limit", 0.0));
}

@Test
void doubleParameterThrowsOnNullName() {
    assertThrows(NullPointerException.class, () -> new DoubleParameter(null, 1.0));
}

@Test
void doubleParameterThrowsOnBlankName() {
    assertThrows(IllegalArgumentException.class, () -> new DoubleParameter("", 1.0));
}
```

- [ ] **Step 2: Run tests to verify they pass**

Run: `mvn test -Dtest=ParameterTest -pl .`
Expected: 12 tests PASS.

- [ ] **Step 3: Commit**

```bash
git add src/test/java/com/ustermetrics/highs4j/ParameterTest.java
git commit -m "Add DoubleParameter tests"
```

---

### Task 4: StringParameter tests

**Files:**
- Modify: `src/test/java/com/ustermetrics/highs4j/ParameterTest.java`

- [ ] **Step 1: Add failing tests for StringParameter**

Append to `ParameterTest.java`:

```java
@Test
void stringParameterReturnsExpectedValues() {
    var p = new StringParameter("solver", "simplex");

    assertEquals("solver", p.name());
    assertEquals("simplex", p.value());
}

@Test
void stringParameterIsParameter() {
    assertInstanceOf(Parameter.class, new StringParameter("solver", "ipm"));
}

@Test
void stringParameterThrowsOnNullName() {
    assertThrows(NullPointerException.class, () -> new StringParameter(null, "x"));
}

@Test
void stringParameterThrowsOnBlankName() {
    assertThrows(IllegalArgumentException.class, () -> new StringParameter(" ", "x"));
}

@Test
void stringParameterThrowsOnNullValue() {
    assertThrows(NullPointerException.class, () -> new StringParameter("solver", null));
}
```

- [ ] **Step 2: Run tests to verify they pass**

Run: `mvn test -Dtest=ParameterTest -pl .`
Expected: 17 tests PASS.

- [ ] **Step 3: Commit**

```bash
git add src/test/java/com/ustermetrics/highs4j/ParameterTest.java
git commit -m "Add StringParameter tests"
```

---

### Task 5: Pattern matching test

**Files:**
- Modify: `src/test/java/com/ustermetrics/highs4j/ParameterTest.java`

- [ ] **Step 1: Add pattern matching test**

Append to `ParameterTest.java`:

```java
@Test
void parameterSupportsPatternMatching() {
    var parameters = java.util.List.<Parameter>of(
            new BooleanParameter("output_flag", false),
            new IntParameter("threads", 4),
            new DoubleParameter("time_limit", 60.0),
            new StringParameter("solver", "simplex")
    );

    for (var p : parameters) {
        var description = switch (p) {
            case BooleanParameter(var name, var value) -> name + "=" + value;
            case IntParameter(var name, var value) -> name + "=" + value;
            case DoubleParameter(var name, var value) -> name + "=" + value;
            case StringParameter(var name, var value) -> name + "=" + value;
        };
        assertNotNull(description);
    }
}
```

- [ ] **Step 2: Run tests to verify they pass**

Run: `mvn test -Dtest=ParameterTest -pl .`
Expected: 18 tests PASS.

- [ ] **Step 3: Commit**

```bash
git add src/test/java/com/ustermetrics/highs4j/ParameterTest.java
git commit -m "Add pattern matching test for Parameter sealed interface"
```

---

### Task 6: Full build verification

- [ ] **Step 1: Run full build with all tests**

Run: `mvn clean verify`
Expected: BUILD SUCCESS, all tests pass.

- [ ] **Step 2: Commit (if any adjustments needed)**

No commit expected unless Task 6 revealed issues.
