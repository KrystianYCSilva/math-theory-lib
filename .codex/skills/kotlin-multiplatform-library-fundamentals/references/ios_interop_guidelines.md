# Kotlin/Native to Swift/Objective-C Interoperability Guidelines

This document lists common pitfalls and best practices when exporting a Kotlin Multiplatform API for consumption in an iOS (Swift/Objective-C) application.

## 1. Generics

**Pitfall:** Kotlin's generics are erased when translated to Objective-C headers. A `List<String>` and a `List<Int>` both become a generic `KotlinMutableList`. Swift sees this as `NSMutableArray` or `Array<AnyObject>`.

**Solution:** Create concrete, non-generic wrapper classes or functions in your Kotlin API surface that will be consumed by Swift.

```kotlin
// KOTLIN (Problem)
fun getItems(): List<String> = listOf("a", "b")

// SWIFT (Resulting problem)
// let items = getItems() // items is of type [AnyObject]
```

```kotlin
// KOTLIN (Solution)
class StringList(val items: List<String>)
fun getItemsWrapped(): StringList = StringList(listOf("a", "b"))

// OR use a more specific return type
fun getItemStrings(): List<String> = listOf("a", "b") // Keep using List
// And in Swift, you'll need to cast, but it's better than nothing.
// let items = getItems() as! [String]
```
A better approach is to use a dedicated framework like `SKIE` which enhances the generated Swift API to be more idiomatic.

## 2. Sealed Classes and Interfaces

**Pitfall:** Sealed classes and interfaces are exposed as a base class with a set of predefined subclasses, but without the exhaustive `when` check that Swift's `enum` provides.

**Solution:** Manually add a common `onEach` or `fold` function to your sealed class hierarchy to simulate exhaustive matching, or use a helper function in Swift.

```kotlin
// KOTLIN
sealed class Result {
    data class Success(val data: String) : Result()
    data class Error(val message: String) : Result()
}
```

```swift
// SWIFT
// You have to handle the default case, which is not ideal.
switch result {
case is Result.Success:
    // ...
case is Result.Error:
    // ...
default:
    fatalError("Unknown Result case")
}
```

Again, tools like `SKIE` can automatically convert sealed hierarchies into Swift enums with associated values.

## 3. Inline and Value Classes

**Pitfall:** `inline` or `value` classes are unboxed in Kotlin but are boxed (wrapped in a class) when exposed to Objective-C/Swift. This can negate the performance benefits and lead to a non-idiomatic API.

**Solution:** Be aware that `value class UserId(val id: String)` in Kotlin becomes a `UserId` class in Swift, not a raw `String`. If you need the raw value, you must access its underlying property (`userId.id`). There is no perfect workaround; this is a fundamental difference in the type systems. Avoid using them in the public API if the boxing is a concern.

## 4. Unsigned Integer Types

**Pitfall:** Kotlin's unsigned types (`UInt`, `ULong`, etc.) do not have a direct equivalent in Objective-C. They are mapped to their signed counterparts (`KotlinInt`, `KotlinLong`), which can lead to incorrect behavior if the values exceed the signed range.

**Solution:** Avoid using unsigned types in your public API that is exposed to iOS. Use standard signed types like `Int` and `Long` and perform any necessary conversions or validations within your Kotlin code.

## 5. Suspend Functions (Coroutines)

**Pitfall:** `suspend` functions are exposed to Swift with completion handlers (`(Result<T, Error>, Unit) -> Void`). While functional, managing nested callbacks can lead to "callback hell."

**Solution:** Starting with Kotlin 1.8.20, you can natively return `Task` objects from Swift's Concurrency model (`async/await`). In your Gradle build, enable this feature:
```kotlin
kotlin {
    sourceSets.all {
        languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
    }
}
```
And use the `@ObjCName` annotation if needed for better naming in Swift.

For older versions, or more complex scenarios, wrap your coroutine calls in a helper class in Kotlin that exposes a more structured API to Swift, potentially using a reactive stream library like `KMP-NativeCoroutines`.
