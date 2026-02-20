package mathsets.ntheory

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class NumberTheoryTest : FunSpec({
    test("extended gcd returns bezout coefficients") {
        val result = ExtendedGcd.compute(240, 46)
        result.gcd shouldBe 2
        result.verifies(240, 46) shouldBe true
    }

    test("modular arithmetic supports inverse and division") {
        ModularArithmetic.add(9, 11, 7) shouldBe 6
        ModularArithmetic.subtract(3, 10, 11) shouldBe 4
        ModularArithmetic.multiply(17, 19, 23) shouldBe 1
        ModularArithmetic.power(7, 20, 13) shouldBe 3
        ModularArithmetic.inverse(3, 11) shouldBe 4
        ModularArithmetic.divide(8, 3, 11) shouldBe 10
    }

    test("crt reconstructs from classical system") {
        val solution = ChineseRemainderTheorem.solve(
            listOf(
                Congruence(2, 3),
                Congruence(3, 5),
                Congruence(2, 7)
            )
        )

        (solution != null) shouldBe true
        solution!!.residue shouldBe 23
        solution.modulus shouldBe 105
        solution.satisfies(
            listOf(
                Congruence(2, 3),
                Congruence(3, 5),
                Congruence(2, 7)
            )
        ) shouldBe true
    }

    test("crt handles system with ten congruences") {
        val x = 1_234_567L
        val moduli = listOf(2L, 3L, 5L, 7L, 11L, 13L, 17L, 19L, 23L, 29L)
        val system = moduli.map { modulus -> Congruence(x % modulus, modulus) }

        val solution = ChineseRemainderTheorem.solve(system)
        (solution != null) shouldBe true
        solution!!.satisfies(system) shouldBe true
    }

    test("miller rabin classifies sample primes and composites") {
        val primes = listOf(2L, 3L, 5L, 7L, 97L, 1_000_000_007L)
        val composites = listOf(1L, 4L, 6L, 8L, 9L, 221L, 341L, 10_000_000_000L)

        primes.all { MillerRabin.isProbablePrime(it) } shouldBe true
        composites.all { !MillerRabin.isProbablePrime(it) } shouldBe true
    }

    test("pollard rho factors composite numbers") {
        PollardRho.factorize(8_051L).shouldContainExactly(83L, 97L)
        PollardRho.factorize(1_001L).shouldContainExactly(7L, 11L, 13L)
    }

    test("arithmetic functions satisfy core identities") {
        ArithmeticFunctions.eulerPhi(13) shouldBe 12
        ArithmeticFunctions.eulerPhi(77) shouldBe 60

        ArithmeticFunctions.mobius(1) shouldBe 1
        ArithmeticFunctions.mobius(30) shouldBe -1
        ArithmeticFunctions.mobius(12) shouldBe 0

        ArithmeticFunctions.tau(36) shouldBe 9
        ArithmeticFunctions.sigma(12) shouldBe 28
    }

    test("quadratic residue symbols are consistent on prime modulus") {
        QuadraticResidue.legendreSymbol(2, 7) shouldBe 1
        QuadraticResidue.legendreSymbol(3, 7) shouldBe -1

        QuadraticResidue.jacobiSymbol(1001, 9907) shouldBe
            QuadraticResidue.legendreSymbol(1001, 9907)
    }

    test("continued fractions build and reconstruct rationals") {
        val terms = ContinuedFraction.fromRational(415, 93)
        terms shouldContainExactly listOf(4L, 2L, 6L, 7L)

        val convergents = ContinuedFraction.convergents(terms)
        convergents.last() shouldBe (415L to 93L)
    }

    test("segmented sieve matches basic sieve") {
        val basic = PrimeGenerator.sieve(1_000)
        val segmented = PrimeGenerator.segmentedSieve(1_000, blockSize = 97)
        segmented shouldContainExactly basic
    }
})
