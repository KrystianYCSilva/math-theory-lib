package mathsets.examples

import mathsets.cardinal.CantorDiagonal
import mathsets.cardinal.Countability
import mathsets.combinatorics.GaleStewartGame
import mathsets.combinatorics.PartitionCalculus
import mathsets.combinatorics.Ramsey
import mathsets.construction.real.ConstructedReal
import mathsets.descriptive.FiniteTopology
import mathsets.forcing.IndependenceDemo
import mathsets.forcing.Poset
import mathsets.function.ChoiceFunction
import mathsets.kernel.NaturalNumber
import mathsets.kernel.RationalNumber
import mathsets.logic.Interpretation
import mathsets.logic.ModelChecker
import mathsets.logic.forAll
import mathsets.ordinal.Ordinal
import mathsets.ordinal.TransfiniteRecursion
import mathsets.ordinal.plus
import mathsets.ordinal.times
import mathsets.relation.EquivalenceRelation
import mathsets.relation.OrderedPair
import mathsets.relation.Relation
import mathsets.relation.TotalOrder
import mathsets.set.ExtensionalSet
import mathsets.set.FiniteModel
import mathsets.set.MathSet
import mathsets.set.ZFCVerifier
import mathsets.set.mathSetOf

/**
 * Advanced use cases demonstrating the full breadth of the math-theory-lib,
 * from equivalence relations and first-order logic to ordinal arithmetic,
 * game theory, topology, and forcing.
 */
object AdvancedUseCases {

    /**
     * A student with a name and numeric grade.
     *
     * @property name The student's name.
     * @property grade The student's grade (0-100).
     */
    data class Student(val name: String, val grade: Int)

    /**
     * Classifies students into grade range groups using an equivalence relation.
     *
     * Students in the same grade range (A: 90-100, B: 80-89, C: 70-79, D: <70) are
     * grouped into equivalence classes, then returned as a labeled map.
     *
     * @param students The list of students to classify.
     * @return A map from grade range labels to lists of students in that range.
     */
    fun classifyStudentsByGrade(students: List<Student>): Map<String, List<Student>> {
        val universe = mathSetOf(students)
        val sameRange: (Student, Student) -> Boolean = { a, b ->
            gradeRange(a.grade) == gradeRange(b.grade)
        }
        val pairs = ExtensionalSet(
            students.flatMap { a ->
                students.filter { b -> sameRange(a, b) }.map { b -> OrderedPair(a, b) }
            }.toSet()
        )
        val relation = Relation(universe, universe, pairs)
        val equiv = EquivalenceRelation(universe, relation)
        val partition = equiv.toPartition()

        return partition.parts.elements().associate { part ->
            val members = part.elements().toList()
            val label = gradeRange(members.first().grade)
            label to members
        }
    }

    private fun gradeRange(grade: Int): String = when {
        grade >= 90 -> "A (90-100)"
        grade >= 80 -> "B (80-89)"
        grade >= 70 -> "C (70-79)"
        else -> "D (<70)"
    }

    /**
     * Verifies a database constraint using first-order logic: "every manager must be certified."
     *
     * Builds a finite interpretation over employees and checks the universal formula
     * using [ModelChecker].
     *
     * @return `true` if all managers are certified.
     */
    fun verifyDatabaseConstraint(): Boolean {
        val employees = setOf<Any?>(1, 2, 3, 4, 5)
        val managers = setOf(3, 5)
        val hasCertification = setOf(1, 3, 4, 5)

        val interpretation = Interpretation(
            universe = employees,
            membership = { element, set ->
                when (set) {
                    "managers" -> element in managers
                    "certified" -> element in hasCertification
                    else -> false
                }
            },
            constants = mapOf("managers" to "managers", "certified" to "certified")
        )

        val constraint = forAll("e") {
            ("e" memberOf "managers") implies ("e" memberOf "certified")
        }

        return ModelChecker.evaluate(constraint, interpretation)
    }

    /**
     * Demonstrates task scheduling using a total order on task names.
     *
     * Defines dependencies among tasks and finds the minimum (first) task.
     *
     * @return A [Pair] of the minimum task and the set of tasks that can run after "compile".
     */
    fun taskScheduling(): Pair<String?, MathSet<String>> {
        val tasks = mathSetOf("compile", "test", "deploy", "docs")
        val deps = listOf(
            "compile" to "compile",
            "test" to "test",
            "deploy" to "deploy",
            "docs" to "docs",
            "compile" to "test",
            "compile" to "deploy",
            "compile" to "docs",
            "test" to "deploy",
            "docs" to "docs"
        )
        val pairs = ExtensionalSet(deps.map { (a, b) -> OrderedPair(a, b) }.toSet())
        val relation = Relation(tasks, tasks, pairs)
        val order = TotalOrder(tasks, relation)

        val first = order.minimum()
        val canRunAfterCompile = mathSetOf("test", "deploy", "docs")
        return Pair(first, canRunAfterCompile)
    }

    /**
     * Elects one delegate from each class using the Axiom of Choice.
     *
     * Applies a [ChoiceFunction] to a family of non-empty sets (classes) and
     * returns the chosen delegate for each.
     *
     * @return A map from class labels to chosen delegate names.
     */
    fun electClassDelegates(): Map<String, String> {
        val classA: MathSet<String> = mathSetOf("Alice", "Bob")
        val classB: MathSet<String> = mathSetOf("Carol", "Dave")
        val classC: MathSet<String> = mathSetOf("Eve", "Frank")

        val family: MathSet<MathSet<String>> = mathSetOf(listOf(classA, classB, classC))
        val choice = ChoiceFunction(family)
        val chosen = choice.choose()

        return chosen.map { (group, delegate) ->
            val label = when {
                "Alice" in group -> "Turma A"
                "Carol" in group -> "Turma B"
                else -> "Turma C"
            }
            label to delegate
        }.toMap()
    }

    /**
     * Demonstrates Cantor's diagonal argument: verifies that no surjection S -> P(S) exists.
     *
     * @return `true` if the diagonal set is not in the image (confirming non-surjectivity).
     */
    fun cantorDiagonalDemo(): Boolean {
        val domain = mathSetOf(1, 2, 3)
        val mapping: (Int) -> MathSet<Int> = { x ->
            when (x) {
                1 -> mathSetOf(1, 2)
                2 -> mathSetOf(2, 3)
                3 -> mathSetOf(1)
                else -> MathSet.empty()
            }
        }
        return CantorDiagonal.verifyNotSurjective(domain, mapping)
    }

    /**
     * Analyzes a simplified Nim game using the Gale-Stewart framework.
     *
     * 3 turns, moves {1, 2}, Player I wins if the sum of moves >= 4.
     *
     * @return A [Triple] of (Player I has winning strategy, Player II has winning strategy,
     *         best opening move for Player I or null).
     */
    fun nimGameAnalysis(): Triple<Boolean, Boolean, Int?> {
        val game = GaleStewartGame(
            legalMoves = listOf(1, 2),
            horizon = 3,
            payoffForFirstPlayer = { history -> history.sum() >= 4 }
        )
        val p1Wins = game.firstPlayerHasWinningStrategy()
        val p2Wins = game.secondPlayerHasWinningStrategy()
        val bestOpening = game.bestMoveFor(emptyList())
        return Triple(p1Wins, p2Wins, bestOpening)
    }

    /**
     * Solves the "party problem" R(3,3) = 6 using Ramsey number search.
     *
     * @return The smallest n such that any 2-coloring of edges of K_n contains
     *         a monochromatic triangle, or `null` if not found within bounds.
     */
    fun partyProblem(): Int? {
        return Ramsey.searchBounds(cliqueSize = 3, colors = 2, maxVertices = 8)
    }

    /**
     * Demonstrates topological operations (interior, closure, boundary) on a finite topology.
     *
     * @return A [Triple] of (interior, closure, boundary) of the set {2, 3} in
     *         the topology on {1, 2, 3, 4}.
     */
    fun topologyAnalysis(): Triple<MathSet<Int>, MathSet<Int>, MathSet<Int>> {
        val universe = mathSetOf(1, 2, 3, 4)
        val openSets: MathSet<MathSet<Int>> = mathSetOf(
            listOf(
                MathSet.empty<Int>(),
                universe,
                mathSetOf(1),
                mathSetOf(1, 2),
                mathSetOf(1, 2, 3)
            )
        )
        val topology = FiniteTopology(universe, openSets)
        val target = mathSetOf(2, 3)
        return Triple(
            topology.interior(target),
            topology.closure(target),
            topology.boundary(target)
        )
    }

    /**
     * Demonstrates that ordinal addition and multiplication are not commutative.
     *
     * @return A [Pair] where `first` indicates whether omega + 1 == 1 + omega (false)
     *         and `second` indicates whether omega * 2 == 2 * omega (false).
     */
    fun ordinalNonCommutativity(): Pair<Boolean, Boolean> {
        val omega = Ordinal.OMEGA
        val one = Ordinal.ONE
        val additionCommutes = (omega + one) == (one + omega)
        val multiplicationCommutes = (omega * Ordinal.finite(2)) == (Ordinal.finite(2) * omega)
        return Pair(additionCommutes, multiplicationCommutes)
    }

    /**
     * Computes a sum via transfinite recursion up to the finite ordinal 10.
     *
     * @return The result of summing 1 at each successor step from 0 to 10 (i.e., 10).
     */
    fun transfiniteSum(): Int {
        return TransfiniteRecursion.transfiniteRecursion(
            ordinal = Ordinal.finite(10),
            base = 0,
            successorCase = { _, prev -> prev + 1 },
            limitCase = { _, approx -> approx.last() }
        )
    }

    /**
     * Demonstrates the independence of a finite analogue of the Continuum Hypothesis.
     *
     * @return A [Pair] where `first` is `true` (CH holds in model 1) and `second` is
     *         `false` (CH fails in model 2).
     */
    fun continuumHypothesisIndependence(): Pair<Boolean, Boolean> {
        return IndependenceDemo.summary()
    }

    /**
     * Verifies ZFC axioms on a mini finite model containing {empty, {1}, {2}, {1,2}}.
     *
     * @return A map from axiom names to whether they hold in the model.
     */
    fun verifyZfcOnMiniModel(): Map<String, Boolean> {
        val empty = MathSet.empty<Int>()
        val s1 = mathSetOf(1)
        val s2 = mathSetOf(2)
        val s12 = mathSetOf(1, 2)
        val universe = ExtensionalSet(setOf(1, 2))
        val sets = ExtensionalSet(setOf<MathSet<Int>>(empty, s1, s2, s12))
        val model = FiniteModel(universe, sets)
        return ZFCVerifier.verify(model).byAxiom
    }

    /**
     * Demonstrates the constructive definition of sqrt(2) as a Cauchy sequence of rationals.
     *
     * @return A [Pair] of the rational approximation (as string) and whether the sequence
     *         satisfies the Cauchy criterion on a finite prefix.
     */
    fun constructiveSqrt2(): Pair<String, Boolean> {
        val sqrt2 = ConstructedReal.squareRootOf(2)
        val isCauchy = sqrt2.isCauchyOnFinitePrefix(10)
        val approx = sqrt2.approximateRational(20)
        return Pair(approx.toString(), isCauchy)
    }

    /**
     * Enumerates the first 10 rationals in the constructive N -> Q bijection.
     *
     * @return A list of strings "i -> q" for i in 0..9.
     */
    fun rationalEnumeration(): List<String> {
        return (0..9).map { i ->
            val n = NaturalNumber.of(i)
            val q = Countability.naturalToRational(n)
            "$i -> $q"
        }
    }

    /**
     * Computes Bell numbers B(0) through B(5), demonstrating partition counting.
     *
     * @return A list of pairs (n, B(n) as string) for n in 0..5.
     */
    fun bellNumbersDemo(): List<Pair<Int, String>> {
        return (0..5).map { n ->
            n to PartitionCalculus.bellNumber(n).toString()
        }
    }

    /**
     * Counts the number of filters on a 3-element poset (preference model).
     *
     * @return The number of filters in the poset A <= B <= C.
     */
    fun preferenceFilters(): Int {
        val options = setOf("A", "B", "C")
        val poset = Poset(options) { a, b ->
            a == b || (a == "A" && b != "A") || (a == "B" && b == "C")
        }
        return poset.filters().size
    }
}
