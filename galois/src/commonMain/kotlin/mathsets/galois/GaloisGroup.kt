package mathsets.galois

import mathsets.algebra.Group

/**
 * The Galois group Gal(L/K) of a field extension L/K, consisting of all
 * K-automorphisms of L.
 *
 * When L/K is a Galois extension (normal and separable), the Galois group
 * has order [L:K]. This class implements [Group] over [FieldAutomorphism]<K>.
 *
 * @param K The base field element type.
 * @property extension The field extension L/K.
 * @property automorphisms All K-automorphisms of L.
 */
class GaloisGroup<K>(
    val extension: FieldExtension<K>,
    val automorphisms: List<FieldAutomorphism<K>>
) : Group<FieldAutomorphism<K>> {

    private val ops = FieldAutomorphismOps(extension)

    /**
     * The order of the Galois group.
     */
    val order: Int = automorphisms.size

    /**
     * Whether the extension is Galois (|Gal(L/K)| = [L:K]).
     */
    val isGalois: Boolean = automorphisms.size == extension.degree

    override val identity: FieldAutomorphism<K> = ops.identity()

    override fun op(a: FieldAutomorphism<K>, b: FieldAutomorphism<K>): FieldAutomorphism<K> =
        ops.compose(a, b)

    override fun inverse(a: FieldAutomorphism<K>): FieldAutomorphism<K> =
        ops.inverse(a, automorphisms)

    /**
     * Returns the fixed field of a subgroup H of Gal(L/K).
     *
     * The fixed field L^H = { x in L | sigma(x) = x for all sigma in H }.
     *
     * @param subgroup The subgroup H (as a list of automorphisms).
     * @param elements All elements of L.
     * @return The set of elements fixed by every automorphism in H.
     */
    fun fixedField(
        subgroup: List<FieldAutomorphism<K>>,
        elements: Set<ExtensionElement<K>>
    ): Set<ExtensionElement<K>> {
        return elements.filter { x ->
            subgroup.all { sigma -> ops.apply(sigma, x) == x }
        }.toSet()
    }

    /**
     * Returns the subgroup of Gal(L/K) that fixes a given intermediate field.
     *
     * Gal(L/M) = { sigma in Gal(L/K) | sigma(m) = m for all m in M }.
     *
     * @param intermediateField The elements of the intermediate field M.
     * @return The list of automorphisms fixing M.
     */
    fun fixingSubgroup(
        intermediateField: Set<ExtensionElement<K>>
    ): List<FieldAutomorphism<K>> {
        return automorphisms.filter { sigma ->
            intermediateField.all { m -> ops.apply(sigma, m) == m }
        }
    }

    /**
     * Computes all subgroups of this Galois group (brute force, for small groups).
     *
     * @return A list of subgroups, each represented as a list of automorphisms.
     */
    fun allSubgroups(): List<List<FieldAutomorphism<K>>> {
        val result = mutableListOf<List<FieldAutomorphism<K>>>()
        result.add(listOf(identity))
        result.add(automorphisms.toList())

        fun generateSubgroup(generators: List<FieldAutomorphism<K>>): List<FieldAutomorphism<K>> {
            val subgroup = mutableSetOf<FieldAutomorphism<K>>()
            subgroup.add(identity)
            subgroup.addAll(generators)

            var changed = true
            while (changed) {
                changed = false
                val current = subgroup.toList()
                for (a in current) {
                    for (b in current) {
                        val product = op(a, b)
                        val matched = subgroup.any { it.alphaImage == product.alphaImage }
                        if (!matched) {
                            subgroup.add(product)
                            changed = true
                        }
                    }
                    val inv = inverse(a)
                    val invMatched = subgroup.any { it.alphaImage == inv.alphaImage }
                    if (!invMatched) {
                        subgroup.add(inv)
                        changed = true
                    }
                }
            }
            return subgroup.toList()
        }

        for (i in automorphisms.indices) {
            val sg = generateSubgroup(listOf(automorphisms[i]))
            if (sg.size > 1 && sg.size < automorphisms.size) {
                val alreadyFound = result.any { existing ->
                    existing.size == sg.size && existing.all { e ->
                        sg.any { s -> s.alphaImage == e.alphaImage }
                    }
                }
                if (!alreadyFound) result.add(sg)
            }
        }

        for (i in automorphisms.indices) {
            for (j in i + 1 until automorphisms.size) {
                val sg = generateSubgroup(listOf(automorphisms[i], automorphisms[j]))
                if (sg.size > 1 && sg.size < automorphisms.size) {
                    val alreadyFound = result.any { existing ->
                        existing.size == sg.size && existing.all { e ->
                            sg.any { s -> s.alphaImage == e.alphaImage }
                        }
                    }
                    if (!alreadyFound) result.add(sg)
                }
            }
        }

        return result
    }

    /**
     * Checks if a subgroup is normal in this Galois group.
     *
     * N is normal in G if gNg^{-1} = N for all g in G.
     *
     * @param subgroup The subgroup to test.
     * @return `true` if the subgroup is normal.
     */
    fun isNormalSubgroup(subgroup: List<FieldAutomorphism<K>>): Boolean {
        for (g in automorphisms) {
            for (n in subgroup) {
                val conjugate = op(op(g, n), inverse(g))
                val inSubgroup = subgroup.any { it.alphaImage == conjugate.alphaImage }
                if (!inSubgroup) return false
            }
        }
        return true
    }

    companion object {
        /**
         * Constructs the Galois group of a finite field extension by
         * enumerating all automorphisms.
         *
         * @param K The base field element type.
         * @param extension The field extension L/K.
         * @param elements All elements of L.
         * @return The Galois group Gal(L/K).
         */
        fun <K> of(
            extension: FieldExtension<K>,
            elements: Set<ExtensionElement<K>>
        ): GaloisGroup<K> {
            val ops = FieldAutomorphismOps(extension)
            val auts = ops.findAllAutomorphisms(elements)
            return GaloisGroup(extension, auts)
        }
    }
}
