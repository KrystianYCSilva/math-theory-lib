package mathsets.kernel

/**
 * A mathematical predicate over a domain `T`.
 *
 * Predicates are the building blocks for defining sets by comprehension
 * (intensional definition). This type alias wraps a simple boolean-valued
 * function `(T) -> Boolean`.
 *
 * Combinator functions [and], [or], [not], [implies], and [iff] provide
 * logical composition of predicates.
 *
 * @param T The domain type of the predicate.
 */
typealias Predicate<T> = (T) -> Boolean

/**
 * Logical conjunction (AND) of two predicates.
 *
 * @param other The predicate to conjoin with this one.
 * @return A new predicate that is `true` when both this and [other] are `true`.
 */
infix fun <T> Predicate<T>.and(other: Predicate<T>): Predicate<T> = { value ->
    this(value) && other(value)
}

/**
 * Logical disjunction (OR) of two predicates.
 *
 * @param other The predicate to disjoin with this one.
 * @return A new predicate that is `true` when either this or [other] is `true`.
 */
infix fun <T> Predicate<T>.or(other: Predicate<T>): Predicate<T> = { value ->
    this(value) || other(value)
}

/**
 * Logical negation (NOT) of a predicate.
 *
 * @return A new predicate that is `true` when this predicate is `false`, and vice versa.
 */
fun <T> Predicate<T>.not(): Predicate<T> = { value ->
    !this(value)
}

/**
 * Material implication of two predicates (P implies Q).
 *
 * Equivalent to `!P || Q` in classical logic.
 *
 * @param other The consequent predicate.
 * @return A new predicate representing `this -> other`.
 */
infix fun <T> Predicate<T>.implies(other: Predicate<T>): Predicate<T> = { value ->
    !this(value) || other(value)
}

/**
 * Biconditional (if and only if) of two predicates.
 *
 * @param other The predicate to compare with.
 * @return A new predicate that is `true` when both predicates have the same truth value.
 */
infix fun <T> Predicate<T>.iff(other: Predicate<T>): Predicate<T> = { value ->
    this(value) == other(value)
}
