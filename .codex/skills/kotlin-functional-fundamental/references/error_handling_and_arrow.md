# error_handling_and_arrow

Kotlin Result
- Use Result<T> to represent success/failure without throwing; use result.fold/onFailure/getOrElse to handle flows.

Option and Either (concepts)
- Option represents presence/absence; Either<E,A> models typed failure or success.
- Use Option for nullable alternatives; Either to carry domain error information.

Arrow quick pragmatics
- Add dependency: implementation("io.arrow-kt:arrow-core:<latest>")
- Example: Either.catch { s.toInt() }.mapLeft { "Invalid int: ${it.message}" }
- Use Validated to accumulate validation errors when needed.

Design guidance
- Convert exceptions at the adapter edge to functional types; propagate and compose using map/flatMap.
- Choose Result for simple flows, Either/Validated for rich domain error modeling.
