# typetheory

Minimal MLTT-style type theory core for Wave 1 (Phase E).

## Includes

- `Type` and `Term` syntax (Universe, Pi, Sigma, Id, Nat, Bool, Empty, Unit)
- Small-step evaluator with beta reduction and `NatRec`
- Bidirectional type checker (`infer` and `check`)
- Minimal Curry-Howard helpers

## Scope

This module intentionally provides a didactic core subset suitable for finite
examples and law-oriented tests.
