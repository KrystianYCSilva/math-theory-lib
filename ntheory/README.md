# ntheory

Number theory foundations for Wave 3.

## Overview

This module introduces exact arithmetic and algorithmic primitives over integers:

- `ModularArithmetic` (mod add/sub/mul/pow/inverse/division)
- `ExtendedGcd` (Bezout coefficients)
- `ChineseRemainderTheorem` (system reconstruction)
- `MillerRabin` (probabilistic primality test with deterministic 64-bit bases)
- `PollardRho` (integer factor search and recursive factorization)
- `ArithmeticFunctions` (`phi`, `mu`, `sigma`, `tau`, prime factorization)
- `QuadraticResidue` (Legendre and Jacobi symbols)
- `ContinuedFraction` (rational expansions and convergents)

## Notes

- Current algorithms target practical 64-bit integer workflows for cross-platform KMP.
- The module is designed to be extended with additional Sprint 35-36 components.
