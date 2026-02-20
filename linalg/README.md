# linalg

Linear algebra foundations for Wave 3.

## Overview

This module starts the Wave 3 algebra-linear stack with exact, field-driven matrix
operations (especially over `RationalNumber`).

Current scope includes:

- `Matrix<K>` (dense) and `SparseMatrix<K>` (map-based sparse form)
- `MatrixLinearAlgebra<K>` for:
  - matrix addition/multiplication/transposition
  - Gaussian elimination to reduced row-echelon form
  - determinant (elimination and Leibniz)
  - matrix inverse
  - linear-system solving (`Unique`, `Infinite`, `Inconsistent`)
- `MatrixRing<K>` for fixed-size square matrices over a field

## Notes

- The implementation is exact when used with exact fields like `RationalField`.
- Law-oriented and acceptance-oriented checks are placed in tests.
