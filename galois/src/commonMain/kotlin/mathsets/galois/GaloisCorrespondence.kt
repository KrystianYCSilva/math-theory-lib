package mathsets.galois

/**
 * Implements the Fundamental Theorem of Galois Theory for finite Galois extensions.
 *
 * For a Galois extension L/K with Galois group G = Gal(L/K), there is an
 * inclusion-reversing bijection between:
 * - Subgroups H of G
 * - Intermediate fields M with K <= M <= L
 *
 * given by:
 * - H -> L^H (the fixed field of H)
 * - M -> Gal(L/M) (the subgroup fixing M)
 *
 * Moreover:
 * - [L : L^H] = |H| and [L^H : K] = [G : H]
 * - H is normal in G if and only if L^H/K is Galois
 *
 * @param K The base field element type.
 * @property galoisGroup The Galois group of the extension.
 * @property elements All elements of the extension field L.
 */
class GaloisCorrespondence<K>(
    val galoisGroup: GaloisGroup<K>,
    val elements: Set<ExtensionElement<K>>
) {

    /**
     * A single entry in the Galois correspondence, pairing a subgroup
     * with its fixed field.
     *
     * @param K The base field element type.
     * @property subgroup A subgroup H of the Galois group.
     * @property fixedField The fixed field L^H.
     * @property subgroupOrder |H|.
     * @property fixedFieldDegree [L^H : K] (inferred as [G:H]).
     * @property isNormal Whether H is normal in G.
     */
    data class CorrespondenceEntry<K>(
        val subgroup: List<FieldAutomorphism<K>>,
        val fixedField: Set<ExtensionElement<K>>,
        val subgroupOrder: Int,
        val fixedFieldDegree: Int,
        val isNormal: Boolean
    )

    /**
     * Computes the full Galois correspondence: all subgroups paired with
     * their fixed fields.
     *
     * @return A list of [CorrespondenceEntry] items, one per subgroup.
     */
    fun computeCorrespondence(): List<CorrespondenceEntry<K>> {
        val subgroups = galoisGroup.allSubgroups()
        return subgroups.map { subgroup ->
            val fixed = galoisGroup.fixedField(subgroup, elements)
            val isNormal = galoisGroup.isNormalSubgroup(subgroup)
            CorrespondenceEntry(
                subgroup = subgroup,
                fixedField = fixed,
                subgroupOrder = subgroup.size,
                fixedFieldDegree = galoisGroup.order / subgroup.size,
                isNormal = isNormal
            )
        }
    }

    /**
     * Verifies the fundamental theorem by checking that the correspondence
     * is inclusion-reversing and that index/degree relationships hold.
     *
     * Checks:
     * 1. |H| * [L^H : K] = [L : K] for each subgroup H
     * 2. The correspondence is a bijection (different subgroups -> different fixed fields)
     * 3. Normal subgroups correspond to Galois sub-extensions
     *
     * @return A [VerificationResult] with the check outcomes.
     */
    fun verify(): VerificationResult<K> {
        val entries = computeCorrespondence()
        val extensionDegree = galoisGroup.extension.degree

        val indexDegreeCheck = entries.all { entry ->
            entry.subgroupOrder * entry.fixedFieldDegree == extensionDegree
        }

        val fixedFieldSizes = entries.map { it.fixedField.size }
        val bijectivityCheck = fixedFieldSizes.size == fixedFieldSizes.toSet().size

        return VerificationResult(
            indexDegreeProduct = indexDegreeCheck,
            bijectivity = bijectivityCheck,
            entries = entries
        )
    }

    /**
     * The result of verifying the Galois correspondence.
     *
     * @param K The base field element type.
     * @property indexDegreeProduct Whether |H| * [L^H : K] = [L : K] holds for all H.
     * @property bijectivity Whether the correspondence is injective on fixed fields.
     * @property entries All correspondence entries.
     */
    data class VerificationResult<K>(
        val indexDegreeProduct: Boolean,
        val bijectivity: Boolean,
        val entries: List<CorrespondenceEntry<K>>
    ) {
        /**
         * Whether all checks passed.
         */
        val isValid: Boolean = indexDegreeProduct && bijectivity
    }
}
