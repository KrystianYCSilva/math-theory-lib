---
name: layered-architecture-design
description: |
  A guide to designing software using a strict, multi-layered architecture with
  acyclic dependencies. This pattern enhances modularity, testability, and
  maintainability by separating concerns into distinct layers. Use when: designing
  module boundaries, resolving circular dependencies, or structuring a codebase.
tags: [architecture, design-patterns, software-design, modularity]
---

# Skill: Layered Architecture Design

This skill explains how to design software using a layered architecture, a foundational pattern for building robust, modular, and maintainable systems. The core principle is to separate code into distinct layers, each with a specific responsibility. Dependencies are strict and acyclic: higher-level layers can depend on lower-level layers, but never the other way around.

This approach is critical for managing complexity, enabling parallel development, and improving testability.

## 1. The Core Problem: Circular Dependencies

In complex systems, components often depend on each other. A `UserService` might need a `Database`, and the `Database` might need a `Logger`. If the `Logger` then needs to know about the current `User` to add context to logs, you create a circular dependency: `UserService` → `Database` → `Logger` → `UserService`.

This "bootstrap circularity" makes the system:
- **Hard to understand:** You can't analyze one part in isolation.
- **Hard to test:** You can't mock one component without pulling in the entire cycle.
- **Hard to maintain:** A change in one component can have ripple effects across the entire system.

## 2. The Solution: Stratification into Layers

The solution is to break the cycle by stratifying the system into ordered layers. Each layer can only depend on layers below it.

A common pattern involves four layers:

```
╔════════════════════════════════════════════════════════════╗
║                   LAYER 3: USER INTERFACE / API            ║
║     (e.g., Web Controllers, CLI Commands, UI Components)   ║
║      - Depends on Application Layer                      - ║
╠════════════════════════════════════════════════════════════╣
║                   LAYER 2: APPLICATION LOGIC               ║
║     (e.g., Use Cases, Services, Business Workflows)        ║
║      - Depends on Domain/Kernel Layer                    - ║
╠════════════════════════════════════════════════════════════╣
║                   LAYER 1: DOMAIN / KERNEL                 ║
║     (e.g., Core Data Structures, Business Objects,        ║
║      Interfaces for external services like repositories)   ║
║      - Has zero external dependencies                    - ║
╠════════════════════════════════════════════════════════════╣
║                   LAYER 0: INFRASTRUCTURE                  ║
║     (e.g., Database implementations, 3rd-party API clients)║
║      - Implements interfaces from the Domain Layer       - ║
╚════════════════════════════════════════════════════════════╝
```
**The Golden Rule:** Dependencies only point downwards. Layer N can use Layer N-1, but N-1 knows nothing about N. The Infrastructure layer is special; it implements interfaces defined in the Domain layer (Dependency Inversion Principle).

---
## 3. Case Study: `mathsets-kt` Architecture

The `mathsets-kt` project provides an excellent, real-world example of solving a fundamental circular dependency in mathematics through layered architecture.

- **The Circularity:** To build numbers, you need sets. To compute with sets, you need numbers (for indexing, counting, etc.).
- **The Layered Solution:**

    - **Layer 0: Kernel** - Provides raw, *computational primitives* (like `BigInteger` wrappers). It has no axiomatic pretensions. It exists so the machine can perform basic operations. It has **no dependencies** on other project modules.

    - **Layer 1: Logic** - Defines the *rules* and *specifications* of the system (e.g., the `PeanoAxioms` interface). It describes *what* a number system should be, but not *how* it is implemented. It depends only on the **Kernel**.

    - **Layer 2: Set Theory** - Provides the main tools for the domain, like `MathSet<T>`, `Relation`, and `Function`. It uses the **Kernel** for computation and the **Logic** layer to define its properties.

    - **Layer 3: Construction** - Uses the tools from the **Set Theory** layer to *formally construct* the number systems (e.g., VonNeumann ordinals). This layer proves that the efficient primitives in the Kernel are consistent with axiomatic foundations. It depends on all lower layers.

### Dependency Graph (`mathsets-kt`)

```
          Kernel/ (L0)
         ╱       ╲
        ╱         ╲
      Logic/ (L1)  │
       │           │
       │         Set/ (L2)
       │        ╱    ╲
       │       ╱      ╲
       Relation/ (L2)  Function/ (L2)
         ╲          ╱
          ╲        ╱
         Construction/ (L3)
```

This strict, acyclic graph ensures that the foundational `kernel` can be built and tested without any knowledge of the abstract mathematical concepts it will later be used to construct.

---
## 4. How to Implement a Layered Architecture

1.  **Identify Your Layers:** Start by categorizing your code. What is core domain logic? What is application-specific workflow? What is UI? What is an external dependency?
2.  **Define Module Boundaries:** In a modern project, each layer should correspond to a separate module, package, or directory (e.g., `kernel/`, `logic/`, `set/` in `mathsets-kt`).
3.  **Enforce Dependency Rules:** Use your build system (e.g., Gradle, Maven, `package.json`) to enforce the dependency graph. Module `A` should not have a dependency on module `B` if the architecture forbids it. This provides compile-time checks.
4.  **Use Dependency Inversion:** When a lower layer needs to "call" an upper layer, it should do so through an interface.
    -   **Example:** The `Application` layer (L2) may need to save data. It should call a `Repository` *interface* defined in the `Domain` layer (L1). The concrete `DatabaseRepository` *implementation* lives in the `Infrastructure` layer (L0) and implements that interface. This inverts the dependency flow and keeps the domain pure.

## References

1.  **Source of this Skill:** [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) from the `mathsets-kt` project.
2.  **General Concept:** [P of EAA: Service Layer](https://martinfowler.com/eaaCatalog/serviceLayer.html) by Martin Fowler.
3.  **Related Pattern:** [Hexagonal Architecture (Ports and Adapters)](https://alistair.cockburn.us/hexagonal-architecture/)
4.  **Related Pattern:** [The Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) by Robert C. Martin.
