package mathsets.forcing

data class ChAnalogueModel(
    val name: String,
    val countableLike: Int,
    val continuumLike: Int,
    val intermediateCardinals: Set<Int>
) {
    fun chAnalogueHolds(): Boolean = intermediateCardinals.none {
        it > countableLike && it < continuumLike
    }
}

/**
 * Demonstração finita de independência análoga à CH:
 * um modelo onde a afirmação vale e outro onde falha.
 */
object IndependenceDemo {
    fun modelWhereChAnalogueHolds(): ChAnalogueModel =
        ChAnalogueModel(
            name = "FiniteModel-CH-True",
            countableLike = 10,
            continuumLike = 20,
            intermediateCardinals = setOf(5, 10, 20, 40)
        )

    fun modelWhereChAnalogueFails(): ChAnalogueModel =
        ChAnalogueModel(
            name = "FiniteModel-CH-False",
            countableLike = 10,
            continuumLike = 20,
            intermediateCardinals = setOf(5, 10, 12, 20, 40)
        )

    fun summary(): Pair<Boolean, Boolean> {
        val trueModel = modelWhereChAnalogueHolds()
        val falseModel = modelWhereChAnalogueFails()
        return trueModel.chAnalogueHolds() to falseModel.chAnalogueHolds()
    }
}

