package mathsets.kernel

/**
 * Predicado matemático sobre um domínio `T`.
 */
typealias Predicate<T> = (T) -> Boolean

infix fun <T> Predicate<T>.and(other: Predicate<T>): Predicate<T> = { value ->
    this(value) && other(value)
}

infix fun <T> Predicate<T>.or(other: Predicate<T>): Predicate<T> = { value ->
    this(value) || other(value)
}

fun <T> Predicate<T>.not(): Predicate<T> = { value ->
    !this(value)
}

infix fun <T> Predicate<T>.implies(other: Predicate<T>): Predicate<T> = { value ->
    !this(value) || other(value)
}

infix fun <T> Predicate<T>.iff(other: Predicate<T>): Predicate<T> = { value ->
    this(value) == other(value)
}

