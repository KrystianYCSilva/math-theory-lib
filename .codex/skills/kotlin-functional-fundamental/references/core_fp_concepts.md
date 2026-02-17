# core_fp_concepts

Higher-Order Functions & Lambdas
- HOF: functions that accept or return functions; enable composition and abstraction.
- inline / noinline / crossinline
  - inline reduces lambda-call overhead; use for small high-frequency lambdas.
  - noinline marks lambdas not to be inlined when passing them as values.
  - crossinline prevents non-local returns inside inline lambdas when necessary.

Collection Pipelines
- map, filter, flatMap, fold: compose small transformations.
- prefer fold(initial) over reduce when an explicit seed is needed.
- example: val total = items.map { it.v }.fold(0) { acc, v -> acc + v }

Sequences vs Iterables
- Iterables/Lists are eager and clear; Sequences are lazy and avoid intermediate allocations in long pipelines.
- Use Sequence for large datasets or when chaining many transformations.

Interop with Java Streams
- Convert carefully between Sequences and Streams; document terminal operations and lifecycle.
- Avoid implicit parallelism mismatches; decide where parallelization occurs.

Performance tips
- Inline tiny, hot HOFs; avoid unnecessary boxing; measure after changes.
