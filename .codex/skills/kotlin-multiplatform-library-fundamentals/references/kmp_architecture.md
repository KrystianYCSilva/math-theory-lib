# KMP Architecture Best Practices

This document outlines architectural best practices for building scalable and maintainable Kotlin Multiplatform libraries.

## Dependency Injection over `expect`/`actual`

While `expect`/`actual` is a powerful mechanism for platform-specific implementations, it can lead to tight coupling between `commonMain` and platform-specific code. For providing platform-specific *capabilities* or *features*, Dependency Injection (DI) is often a more flexible and testable approach.

**Problem:** `expect`/`actual` creates a compile-time dependency. Your common module *knows* that a platform-specific implementation must exist.

```kotlin
// commonMain
expect fun getPlatformName(): String

// jvmMain
actual fun getPlatformName(): String = "JVM"

// usage in commonMain
fun createReport() {
    val platform = getPlatformName() // Direct call, tight coupling
    println("Report generated on $platform")
}
```

**Solution:** Define an interface in `commonMain` and provide the implementation from the platform-specific code at runtime.

```kotlin
// commonMain
interface Platform {
    val name: String
}

class ReportGenerator(private val platform: Platform) {
    fun createReport() {
        println("Report generated on ${platform.name}")
    }
}

// jvmMain
class JvmPlatform : Platform {
    override val name: String = "JVM"
}

// In your JVM application's entry point
fun main() {
    val jvmPlatform = JvmPlatform()
    val reportGenerator = ReportGenerator(jvmPlatform) // Injecting the dependency
    reportGenerator.createReport()
}
```

### When to Use `expect`/`actual`

`expect`/`actual` is still the best choice for:
-   **Low-level primitives:** Accessing system properties, file systems, or specific hardware features that have a direct, low-level counterpart on each platform.
-   **Type Aliases:** Creating platform-specific type aliases (e.g., `typealias AtomicRef<T> = java.util.concurrent.atomic.AtomicReference<T>`).
-   **Annotations:** Defining annotations that have different retention policies or targets per platform.

## State Immutability and Concurrency

To ensure thread safety, especially in a multi-threaded environment like Kotlin/Native, always prefer immutable state.

-   **Use `val` by default:** Declare properties with `val` unless mutability is essential.
-   **Use Immutable Collections:** Prefer `listOf()`, `setOf()`, `mapOf()` over their mutable counterparts.
-   **Data Classes are your Friend:** Data classes automatically generate `equals()`, `hashCode()`, and `toString()` based on their `val` properties, encouraging immutability.
-   **State Transformation:** When state needs to change, create a new state object instead of mutating the existing one.

**Example:**

```kotlin
// ANTI-PATTERN: Mutable state is dangerous in concurrent scenarios
class MutableState {
    var count = 0
    fun increment() {
        count++ // Not thread-safe
    }
}

// PREFERRED: Immutable state
data class CounterState(val count: Int)

fun increment(state: CounterState): CounterState {
    return state.copy(count = state.count + 1) // Returns a new, immutable instance
}
```
