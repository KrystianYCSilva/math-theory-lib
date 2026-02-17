package mathsets.set

/**
 * Thrown when an operation attempts to eagerly collect the elements of an infinite
 * or uncountable [MathSet] into a finite [ExtensionalSet].
 *
 * @param message a description of which set could not be materialized.
 */
class InfiniteMaterializationException(message: String) : IllegalStateException(message)
