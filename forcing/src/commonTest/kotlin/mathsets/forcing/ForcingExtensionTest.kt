package mathsets.forcing

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ForcingExtensionTest : FunSpec({
    test("builds finite forcing extension universe") {
        val generic = GenericFilter(setOf(0, 1))
        val extension = ForcingExtension(groundModel = setOf("a", "b"), genericFilter = generic)

        val universe = extension.extensionUniverse()
        universe.size shouldBe 2
        extension.interpret("a").support shouldBe setOf(0, 1)
    }
})

