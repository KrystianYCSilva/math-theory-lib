KDoc Syntax Reference

Overview:
KDoc is the standard documentation comment format for Kotlin. Use /** */ block comments placed immediately before declarations.

Basic tags:
- @param name — describes a function parameter
- @return — describes the return value
- @throws or @exception — documents thrown exceptions
- @sample — references sample code

Examples:
/**
 * Calculates the factorial of n.
 *
 * @param n the non-negative integer to compute the factorial for
 * @return the factorial of n
 * @throws IllegalArgumentException if n < 0
 */
fun factorial(n: Int): Long { /*...*/ }

Formatting tips:
- Keep first sentence as a short summary; Dokka uses it as a summary in listings.
- Use markdown for code fences and lists inside KDoc.
- Prefer examples under an Example: section or use @sample to link external samples.