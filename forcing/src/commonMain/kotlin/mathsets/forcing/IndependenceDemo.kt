package mathsets.forcing

/**
 * A finite analogue model for demonstrating the independence of the Continuum Hypothesis (CH).
 *
 * This model uses finite integers to represent cardinalities, with [countableLike] and
 * [continuumLike] playing the roles of aleph-0 and the continuum. The CH analogue holds
 * if there are no intermediate cardinals strictly between [countableLike] and [continuumLike].
 *
 * @property name A descriptive name for this model.
 * @property countableLike The integer playing the role of aleph-0.
 * @property continuumLike The integer playing the role of the continuum.
 * @property intermediateCardinals The set of cardinals present in this model.
 */
data class ChAnalogueModel(
    val name: String,
    val countableLike: Int,
    val continuumLike: Int,
    val intermediateCardinals: Set<Int>
) {
    /**
     * Checks whether the CH analogue holds in this model.
     *
     * @return `true` if no intermediate cardinal lies strictly between [countableLike]
     *         and [continuumLike].
     */
    fun chAnalogueHolds(): Boolean = intermediateCardinals.none {
        it > countableLike && it < continuumLike
    }
}

/**
 * Demonstrates the independence of a finite analogue of the Continuum Hypothesis.
 *
 * Constructs two models:
 * - One where the CH analogue holds (no intermediate cardinals between countable and continuum).
 * - One where the CH analogue fails (an intermediate cardinal exists).
 *
 * This mirrors Cohen's proof that CH is independent of ZFC, where forcing produces
 * a model in which CH fails while Godel's constructible universe provides one where it holds.
 */
object IndependenceDemo {
    /**
     * Constructs a model where the CH analogue holds.
     *
     * @return A [ChAnalogueModel] with no intermediate cardinals between countable and continuum.
     */
    fun modelWhereChAnalogueHolds(): ChAnalogueModel =
        ChAnalogueModel(
            name = "FiniteModel-CH-True",
            countableLike = 10,
            continuumLike = 20,
            intermediateCardinals = setOf(5, 10, 20, 40)
        )

    /**
     * Constructs a model where the CH analogue fails.
     *
     * @return A [ChAnalogueModel] with an intermediate cardinal (12) between
     *         countable (10) and continuum (20).
     */
    fun modelWhereChAnalogueFails(): ChAnalogueModel =
        ChAnalogueModel(
            name = "FiniteModel-CH-False",
            countableLike = 10,
            continuumLike = 20,
            intermediateCardinals = setOf(5, 10, 12, 20, 40)
        )

    /**
     * Returns a summary pair showing that the CH analogue holds in one model
     * and fails in another, demonstrating independence.
     *
     * @return A [Pair] where `first` is the result for the CH-true model and
     *         `second` is the result for the CH-false model.
     */
    fun summary(): Pair<Boolean, Boolean> {
        val trueModel = modelWhereChAnalogueHolds()
        val falseModel = modelWhereChAnalogueFails()
        return trueModel.chAnalogueHolds() to falseModel.chAnalogueHolds()
    }
}
