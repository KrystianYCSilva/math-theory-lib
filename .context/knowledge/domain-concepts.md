---
description: |
  Domain concepts and glossary.
  Use when: needing to understand specific business or technical terms.
---

# Domain Concepts

## Core Mathematical Concepts

*   **ZFC (Zermelo-Fraenkel Set Theory with Choice):** The foundational axiomatic system for mathematics upon which this library's constructions are built. Everything is formally a set.
*   **Axiom of Extensionality:** Two sets are equal if and only if they contain the same elements.
*   **Dual Mode Principle:** The core philosophy of this library. Every domain concept (e.g., Integer) has two representations:
    *   **Kernel:** A highly optimized, pragmatic implementation (e.g., wrapping `BigInteger`).
    *   **Construction:** A rigorous representation built purely from ZFC axioms (e.g., ordered pairs of natural numbers modulo an equivalence relation).
*   **Isomorphism:** A mapping between the Kernel representation and the Construction representation that preserves all structural properties, proving that the efficient Kernel is mathematically sound.
*   **Intensional Set vs Extensional Set:**
    *   *Extensional Set:* Defined explicitly by its elements in memory (e.g., `{1, 2, 3}`).
    *   *Intensional Set:* Defined implicitly by a rule or predicate (e.g., $\{ x \in \mathbb{N} \mid x \text{ is even} \}$), necessary for infinite sets to avoid memory exhaustion.
*   **Lazy Evaluation:** Using sequences/iterators to compute elements of infinite sets only when requested.
*   **Von Neumann Natural Numbers:** The set-theoretic construction of natural numbers where $0 = \emptyset$ and $n+1 = n \cup \{n\}$.
*   **Cauchy Sequence:** A sequence whose elements become arbitrarily close to each other. Used to construct Real numbers ($\mathbb{R}$) from Rationals ($\mathbb{Q}$).
*   **Cantor Normal Form (CNF):** A unique representation for ordinal numbers, allowing arithmetic beyond infinity.

## Architecture Concepts

*   **Context-Oriented Algebra:** Algebraic operations (addition, multiplication) do not belong to the elements themselves, but to an algebraic context (e.g., `IntegerRing`).
*   **Property-Based Testing:** Tests that verify universal laws (e.g., $a + b = b + a$) across hundreds of randomized inputs rather than hardcoding specific values.
