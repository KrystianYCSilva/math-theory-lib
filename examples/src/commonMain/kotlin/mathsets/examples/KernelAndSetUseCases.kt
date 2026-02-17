package mathsets.examples

import mathsets.function.Bijection
import mathsets.kernel.ComplexNumber
import mathsets.kernel.ExtendedReal
import mathsets.kernel.RealNumber
import mathsets.kernel.toExtendedReal
import mathsets.set.MathSet
import mathsets.set.mathSetOf

object KernelAndSetUseCases {
    /**
     * Caso de uso base para análise: comportamento de 1/x com suporte a infinito.
     */
    fun reciprocal(value: RealNumber): ExtendedReal = ExtendedReal.ONE / value.toExtendedReal()

    /**
     * f(x) = x^2 em quociente de diferenças com suporte a x = 0.
     * Para x = 0, retorna indeterminate (0/0).
     */
    fun squareDifferenceQuotient(delta: RealNumber): ExtendedReal {
        val numerator = (delta * delta) - RealNumber.ZERO
        return numerator.toExtendedReal() / delta.toExtendedReal()
    }

    /**
     * Raízes complexas de x^2 + 1 = 0.
     */
    fun rootsOfXSquarePlusOne(): Pair<ComplexNumber, ComplexNumber> =
        Pair(ComplexNumber.I, -ComplexNumber.I)

    /**
     * Particiona um conjunto finito em pares e ímpares.
     */
    fun partitionByParity(values: Iterable<Int>): Pair<MathSet<Int>, MathSet<Int>> {
        val base = mathSetOf(values)
        val evens = base.filter { it % 2 == 0 }
        val odds = base.filter { it % 2 != 0 }
        return Pair(evens, odds)
    }

    /**
     * Exemplo mínimo de roundtrip em uma bijeção finita.
     */
    fun bijectionRoundTrip(input: Int): Int {
        val domain = mathSetOf(1, 2, 3)
        val codomain = mathSetOf("a", "b", "c")

        val bijection = Bijection(domain, codomain) { n ->
            when (n) {
                1 -> "a"
                2 -> "b"
                3 -> "c"
                else -> error("Out of domain")
            }
        }

        return bijection.inverse()(bijection(input))
    }
}

