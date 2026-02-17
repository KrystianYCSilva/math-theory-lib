package mathsets.set

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import mathsets.kernel.NaturalNumber

class UniversalSetsTest : FunSpec({
    test("filters over naturals keep lazy semantics and membership works") {
        val evens = Naturals.filter { it.isEven() }
        val primes = Naturals.filter { it.isPrime() }

        (NaturalNumber.of(4) in evens) shouldBe true
        (NaturalNumber.of(7) in primes) shouldBe true

        val firstFive = evens.elements().take(5).toList()
        firstFive shouldContainExactly listOf(
            NaturalNumber.of(0),
            NaturalNumber.of(2),
            NaturalNumber.of(4),
            NaturalNumber.of(6),
            NaturalNumber.of(8)
        )
    }

    test("materializing infinite set throws dedicated exception") {
        shouldThrow<InfiniteMaterializationException> {
            Naturals.materialize()
        }
    }

    test("materializing filtered finite domain returns extensional set") {
        val finiteDomain = mathSetOf((1..100).map { NaturalNumber.of(it) })
        val evens = finiteDomain.filter { it.isEven() }.materialize()
        evens.elements().toList().size shouldBe 50
        (NaturalNumber.of(100) in evens) shouldBe true
        (NaturalNumber.of(99) in evens) shouldBe false
    }
})
