# solver

SAT/SMT solvers and verification tools for Wave 4.

## Overview

This module provides:

- `SatSolver` — Boolean satisfiability solver (DPLL)
- `PropositionalLogic` — CNF conversion, Tseitin transformation
- `SmtSolver` — SMT solving interface (placeholder for Z3 integration)

## Notes

- SAT solver uses DPLL algorithm with unit propagation
- SMT integration requires external Z3 bindings
