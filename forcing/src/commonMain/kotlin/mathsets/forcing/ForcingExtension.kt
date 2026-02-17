package mathsets.forcing

data class ForcedName<M, C>(
    val groundElement: M,
    val support: Set<C>
)

/**
 * Simulador finito de extensão de forcing M -> M[G].
 */
class ForcingExtension<M, C>(
    private val groundModel: Set<M>,
    private val genericFilter: GenericFilter<C>
) {
    fun extensionUniverse(): Set<ForcedName<M, C>> =
        groundModel.mapTo(linkedSetOf()) { element ->
            ForcedName(element, genericFilter.conditions)
        }

    fun interpret(element: M): ForcedName<M, C> {
        require(element in groundModel) { "Element must belong to the ground model." }
        return ForcedName(element, genericFilter.conditions)
    }
}

