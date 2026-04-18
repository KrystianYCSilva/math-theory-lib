# Architectural Rules

## R1: Zero Mutable State
All objects are immutable after creation. Use `val`, never `var`. Read-only collections or `Sequence<T>`, never `MutableList`.

## R2: Sealed Hierarchies for Domain Closure
Use `sealed class`/`sealed interface` for closed domains (number types, cardinality, formulas) to guarantee exhaustive `when`.

## R3: Strict Layering
Lower modules cannot depend on higher modules. Dependency arrow always points down the mathematical stack.

## R4: Mathematical Rigor
Division by zero, sqrt of negative reals must explicitly throw `MathArithmeticException` or return a defined alternative (e.g., lift to ComplexField). No implicit behaviors.

## R5: Roundtrip Isomorphism Tests
Every axiomatic Construction must pair with a Kernel isomorphism. Tests: generate in Kernel → map to Construction → operate → map back → verify unchanged.

## R6: Property-Based Testing
Verify universal algebraic properties (associativity, De Morgan) over randomized inputs, not just edge cases.

## R7: @JvmInline Value Classes
Use `@JvmInline value class` for kernel primitive wrappers (zero overhead).
