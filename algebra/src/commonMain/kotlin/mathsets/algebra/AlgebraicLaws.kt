package mathsets.algebra

/**
 * Utility for verifying algebraic laws on finite sets of elements.
 *
 * Provides brute-force verification of associativity, commutativity, identity,
 * inverse, and distributivity laws. Useful for testing algebraic structures
 * on small finite domains.
 */
object AlgebraicLaws {

    fun <T> verifyAssociativity(magma: Magma<T>, elements: Set<T>): Boolean =
        elements.all { a ->
            elements.all { b ->
                elements.all { c ->
                    magma.op(magma.op(a, b), c) == magma.op(a, magma.op(b, c))
                }
            }
        }

    fun <T> verifyCommutativity(magma: Magma<T>, elements: Set<T>): Boolean =
        elements.all { a ->
            elements.all { b ->
                magma.op(a, b) == magma.op(b, a)
            }
        }

    fun <T> verifyIdentity(monoid: Monoid<T>, elements: Set<T>): Boolean =
        elements.all { a ->
            monoid.op(a, monoid.identity) == a && monoid.op(monoid.identity, a) == a
        }

    fun <T> verifyInverse(group: Group<T>, elements: Set<T>): Boolean =
        elements.all { a ->
            group.op(a, group.inverse(a)) == group.identity &&
                group.op(group.inverse(a), a) == group.identity
        }

    fun <T> verifyClosure(magma: Magma<T>, elements: Set<T>): Boolean =
        elements.all { a ->
            elements.all { b ->
                magma.op(a, b) in elements
            }
        }

    fun <T> verifyGroupAxioms(group: Group<T>, elements: Set<T>): Boolean =
        verifyClosure(group, elements) &&
            verifyAssociativity(group, elements) &&
            verifyIdentity(group, elements) &&
            verifyInverse(group, elements)

    fun <T> verifyAbelianGroupAxioms(group: AbelianGroup<T>, elements: Set<T>): Boolean =
        verifyGroupAxioms(group, elements) &&
            verifyCommutativity(group, elements)

    fun <T> verifyDistributivity(ring: Ring<T>, elements: Set<T>): Boolean =
        elements.all { a ->
            elements.all { b ->
                elements.all { c ->
                    ring.multiply(a, ring.add(b, c)) == ring.add(ring.multiply(a, b), ring.multiply(a, c)) &&
                        ring.multiply(ring.add(a, b), c) == ring.add(ring.multiply(a, c), ring.multiply(b, c))
                }
            }
        }

    fun <T> verifyRingAxioms(ring: Ring<T>, elements: Set<T>): Boolean {
        val addGroup = ring.additiveGroup()
        return verifyAbelianGroupAxioms(addGroup, elements) &&
            verifyAssociativity(ring.multiplicativeMonoid(), elements) &&
            verifyIdentity(ring.multiplicativeMonoid(), elements) &&
            verifyDistributivity(ring, elements)
    }

    fun <T> verifyFieldAxioms(field: Field<T>, elements: Set<T>): Boolean {
        if (!verifyRingAxioms(field, elements)) return false
        val nonZero = elements.filter { it != field.zero }.toSet()
        return nonZero.all { a ->
            field.multiply(a, field.reciprocal(a)) == field.one
        } && verifyCommutativity(field.multiplicativeMonoid(), elements)
    }

    fun <T> verifyLagrangeTheorem(
        group: Group<T>,
        groupElements: Set<T>,
        subgroup: Subgroup<T>
    ): Boolean {
        val groupOrder = groupElements.size
        val subgroupOrder = subgroup.order()
        return groupOrder % subgroupOrder == 0
    }
}
