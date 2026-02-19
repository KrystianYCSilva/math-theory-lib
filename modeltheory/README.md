# modeltheory

Finite model-theory core for Wave 1 (Phase E).

## Includes

- `Signature` with function, relation, and constant symbols
- `Structure<T>` with finite universe and symbol interpretations
- `Satisfaction<T>` evaluator for `logic.Formula` (`M |= phi`)
- `ElementaryEquivalence` checker on sampled sentence sets
- `Embedding` finite checker (injectivity + signature preservation)
