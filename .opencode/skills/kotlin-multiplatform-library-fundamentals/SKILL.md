---
name: kotlin-multiplatform-library-fundamentals
description: |
  Guides the creation of robust Kotlin Multiplatform (KMP) libraries with focus on API design and cross-platform compatibility.
  Use when: you need to create a new KMP module following best practices for JVM, iOS, and JS targets.
---

# Skill: Kotlin Multiplatform Library Fundamentals

This skill provides a structured workflow for creating high-quality Kotlin Multiplatform (KMP) libraries. It ensures best practices for API design, build configuration, and platform compatibility.

## How to Initialize a KMP Module Structure

### Create Standard Directory Layout

Follow the conventional source set structure:

```
module-name/
├── src/
│   ├── commonMain/kotlin/      # Shared API and logic
│   ├── commonTest/kotlin/      # Shared tests
│   ├── jvmMain/kotlin/         # JVM-specific implementation
│   ├── jvmTest/kotlin/
│   ├── iosMain/kotlin/         # iOS-specific implementation
│   └── jsMain/kotlin/          # JS-specific implementation
├── build.gradle.kts
└── README.md
```

### Configure build.gradle.kts

Key configurations for mathsets-kt modules:

```kotlin
plugins {
    kotlin("multiplatform")
    `maven-publish`
}

kotlin {
    // JVM target with Java 8 compatibility
    jvm {
        withJava()
        jvmToolchain(8)
    }
    
    // iOS targets
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    // JavaScript target
    js(IR) {
        browser()
        nodejs()
    }
    
    // Enforce explicit API mode
    explicitApi()
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                // Shared dependencies
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.kotest:kotest-framework-engine:5.9.1")
                implementation("io.kotest:kotest-property:5.9.1")
            }
        }
    }
}
```

## How to Design the Shared API (commonMain)

### Prioritize Dependency Injection Over expect/actual

Avoid overusing `expect`/`actual`. Instead, pass interfaces into common code:

```kotlin
// GOOD: Interface passed from platform layer
interface PlatformLogger {
    fun log(message: String)
}

class MathematicsEngine(private val logger: PlatformLogger) {
    fun compute() {
        logger.log("Computing...")
        // Common logic here
    }
}

// Platform-specific implementation (jvmMain)
class JvmLogger : PlatformLogger {
    override fun log(message: String) = println("[JVM] $message")
}
```

### Embrace Immutability

Design data classes with `val` properties:

```kotlin
@JvmInline
value class NaturalNumber(val value: ULong)

data class ExtensionalSet<T>(
    private val elements: Set<T> // Immutable backing
) {
    fun add(element: T): ExtensionalSet<T> =
        ExtensionalSet(elements + element) // Returns new instance
}
```

### Use Value Classes for Zero-Cost Wrappers

Wrap primitives for type safety without runtime overhead:

```kotlin
@JvmInline
value class Dimension(val value: Int)

@JvmInline
value class MatrixIndex(val row: Int, val col: Int)
```

## How to Manage Dependencies

### Declare Shared Dependencies in commonMain

```kotlin
sourceSets {
    val commonMain by getting {
        dependencies {
            // Core Kotlin stdlib (automatic)
            
            // Property-based testing
            api("io.kotest:kotest-framework-engine:5.9.1")
            api("io.kotest:kotest-property:5.9.1")
            
            // Project modules
            api(project(":kernel"))
            api(project(":logic"))
        }
    }
}
```

### Platform-Specific Dependencies

Declare platform-specific deps in respective source sets:

```kotlin
val jvmMain by getting {
    dependencies {
        // JVM-specific libraries
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm")
    }
}

val iosMain by getting {
    dependencies {
        // iOS-specific libraries (if needed)
    }
}
```

## How to Ensure Java Interoperability

### Add @Jvm* Annotations

For JVM-targeted APIs, add ergonomic annotations:

```kotlin
/**
 * Computes the union of two sets.
 */
@JvmName("unionOf")
infix fun <T> ExtensionalSet<T>.union(other: ExtensionalSet<T>): ExtensionalSet<T>

// Nullable interoperability
fun findElement(predicate: (T) -> Boolean): T? = TODO()

// SAM conversion for Java
fun interface Predicate<T> {
    fun test(element: T): Boolean
}
```

### Expose KDoc Documentation

Always document public APIs:

```kotlin
/**
 * Represents a mathematical set defined by extension (explicit enumeration).
 *
 * @param elements The immutable set of elements.
 * @throws IllegalArgumentException if duplicate elements are provided.
 */
class ExtensionalSet<T>(private val elements: Set<T>) { ... }
```

## Best Practices for mathsets-kt

1. **Keep commonMain pure** - No platform-specific code
2. **Use Sequence<T>** for lazy evaluation of infinite structures
3. **Sealed interfaces** for closed universes (Cardinality, Ordinal, etc.)
4. **Property-based tests** in commonTest for algebraic laws
5. **Explicit API mode** to prevent accidental API leaks

## References

1. **Source:** `.codex/skills/kotlin-multiplatform-library-fundamentals/SKILL.md`
2. **Official Guide:** [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
3. **API Design:** [Kotlin API Guidelines](https://kotlinlang.org/docs/api-guidelines-introduction.html)
4. **Project Context:** `docs/ARCHITECTURE.md` (Layered Architecture)
