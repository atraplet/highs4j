# Parameter Sealed Interface Design

## Summary

Implement HiGHS solver options as a sealed interface hierarchy in Java. HiGHS options have four
types (boolean, integer, double, string) and are set via string-keyed C API functions. The Java
design uses a sealed `Parameter` interface with four record permits — one per type.

## Context

- HiGHS exposes ~145 options via `Highs_setBoolOptionValue`, `Highs_setIntOptionValue`,
  `Highs_setDoubleOptionValue`, and `Highs_setStringOptionValue` — all keyed by option name string.
- No `Parameters` wrapper class — consumers pass `List<Parameter>` directly to `Model` (when
  implemented).
- Validation of option names is delegated to HiGHS (pass-through), keeping Java forward-compatible.

## Design

### Sealed Interface

`Parameter.java` — sealed interface in `com.ustermetrics.highs4j`:

```java
public sealed interface Parameter permits BooleanParameter, IntParameter, DoubleParameter, StringParameter {
    String name();
}
```

### Record Permits

Four top-level records, one per HiGHS option type:

**`BooleanParameter.java`**

```java
public record BooleanParameter(@NonNull String name, boolean value) implements Parameter {
    public BooleanParameter { checkArgument(!name.isBlank()); }
}
```

**`IntParameter.java`**

```java
public record IntParameter(@NonNull String name, int value) implements Parameter {
    public IntParameter { checkArgument(!name.isBlank()); }
}
```

**`DoubleParameter.java`**

```java
public record DoubleParameter(@NonNull String name, double value) implements Parameter {
    public DoubleParameter { checkArgument(!name.isBlank()); }
}
```

**`StringParameter.java`**

```java
public record StringParameter(@NonNull String name, @NonNull String value) implements Parameter {
    public StringParameter { checkArgument(!name.isBlank()); }
}
```

### Validation

- `name` is validated as non-null (Lombok `@NonNull`) and non-blank (Guava `checkArgument`).
- `StringParameter.value` is validated as non-null (Lombok `@NonNull`).
- Option name validity is **not** checked in Java — HiGHS handles this at the native layer.

### Integration with Model (future)

When `Model` is implemented, it will accept `List<Parameter>` and apply them via pattern matching
switch over the sealed interface, dispatching to the appropriate `Highs_set*OptionValue` binding:

```java
void setParameters(List<Parameter> parameters) {
  for (val p : parameters) {
      val status = switch (p) {
          case BooleanParameter(val name, val value) ->
              Highs_setBoolOptionValue(highsSeg, arena.allocateFrom(name), value ? 1 : 0);
          case IntParameter(val name, val value) ->
              Highs_setIntOptionValue(highsSeg, arena.allocateFrom(name), value);
          case DoubleParameter(val name, val value) ->
              Highs_setDoubleOptionValue(highsSeg, arena.allocateFrom(name), value);
          case StringParameter(val name, val value) ->
              Highs_setStringOptionValue(highsSeg, arena.allocateFrom(name), arena.allocateFrom(value));
      };
      // check status
  }
}
```

## Files

| File | Type |
|------|------|
| `src/main/java/.../Parameter.java` | Sealed interface |
| `src/main/java/.../BooleanParameter.java` | Record |
| `src/main/java/.../IntParameter.java` | Record |
| `src/main/java/.../DoubleParameter.java` | Record |
| `src/main/java/.../StringParameter.java` | Record |
| `src/test/java/.../ParameterTest.java` | Tests |

## Tests

`ParameterTest.java` covers:

- **Construction**: each record returns expected `name()` and value accessor.
- **Sealed hierarchy**: each record `instanceof Parameter`.
- **Pattern matching**: exhaustive switch over all four permits.
- **Validation**: blank/null name rejected; `StringParameter` null value rejected.
