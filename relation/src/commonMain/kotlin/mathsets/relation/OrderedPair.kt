package mathsets.relation

import mathsets.set.ExtensionalSet

/**
 * Represents an ordered pair (a, b) in the set-theoretic sense.
 *
 * Unlike an unordered set {a, b}, the ordered pair distinguishes the roles of its
 * components: `first` and `second`. Two ordered pairs are equal if and only if their
 * corresponding components are equal: (a₁, b₁) = (a₂, b₂) ⟺ a₁ = a₂ ∧ b₁ = b₂.
 *
 * @param A the type of the first component
 * @param B the type of the second component
 * @property first the first component of the pair
 * @property second the second component of the pair
 */
data class OrderedPair<A, B>(val first: A, val second: B) {
    /**
     * Converts this ordered pair to its **Kuratowski encoding**.
     *
     * The Kuratowski definition encodes (a, b) as the set {{a}, {a, b}}, reducing
     * the notion of "order" to pure set membership. This is the standard ZFC
     * construction that ensures `(a, b) = (c, d) ⟺ a = c ∧ b = d`.
     *
     * @return a [MathSet] representing `{{a}, {a, b}}`
     */
    fun toKuratowski(): mathsets.set.MathSet<mathsets.set.MathSet<Any?>> {
        val aSet: mathsets.set.MathSet<Any?> = ExtensionalSet(setOf(first as Any?))
        val abSet: mathsets.set.MathSet<Any?> = ExtensionalSet(setOf(first as Any?, second as Any?))
        return ExtensionalSet(setOf(aSet, abSet))
    }
}
