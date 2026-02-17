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

object AdvancedUseCases {

    // ─────────────────────────────────────────────────────────
    // 1. CLASSIFICAÇÃO POR EQUIVALÊNCIA
    //    Agrupar alunos por faixa de nota usando relação de equivalência
    // ─────────────────────────────────────────────────────────

    data class Student(val name: String, val grade: Int)

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

    // ─────────────────────────────────────────────────────────
    // 2. VERIFICAÇÃO DE PROPRIEDADES COM LÓGICA DE PRIMEIRA ORDEM
    //    "Todo gerente deve ser certificado" — verificado sobre modelo finito
    // ─────────────────────────────────────────────────────────

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

    // ─────────────────────────────────────────────────────────
    // 3. SCHEDULING COM ORDEM TOTAL
    //    Definir dependências entre tarefas e encontrar a primeira
    // ─────────────────────────────────────────────────────────

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

    // ─────────────────────────────────────────────────────────
    // 4. AXIOMA DA ESCOLHA — ELEGER DELEGADOS DE TURMA
    // ─────────────────────────────────────────────────────────

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

    // ─────────────────────────────────────────────────────────
    // 5. CANTOR DIAGONAL — PROVAR QUE NÃO EXISTE SURJEÇÃO S → P(S)
    // ─────────────────────────────────────────────────────────

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

    // ─────────────────────────────────────────────────────────
    // 6. TEORIA DE JOGOS — NIM SIMPLIFICADO VIA GALE-STEWART
    //    3 turnos, movimentos {1,2}, P1 ganha se soma >= 4
    // ─────────────────────────────────────────────────────────

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

    // ─────────────────────────────────────────────────────────
    // 7. RAMSEY — "PARTY PROBLEM": R(3,3) = 6
    // ─────────────────────────────────────────────────────────

    fun partyProblem(): Int? {
        return Ramsey.searchBounds(cliqueSize = 3, colors = 2, maxVertices = 8)
    }

    // ─────────────────────────────────────────────────────────
    // 8. TOPOLOGIA COMPUTACIONAL — INTERIOR, FECHO, FRONTEIRA
    // ─────────────────────────────────────────────────────────

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

    // ─────────────────────────────────────────────────────────
    // 9. ORDINAIS — ARITMÉTICA NÃO COMUTATIVA + RECURSÃO TRANSFINITA
    // ─────────────────────────────────────────────────────────

    fun ordinalNonCommutativity(): Pair<Boolean, Boolean> {
        val omega = Ordinal.OMEGA
        val one = Ordinal.ONE
        val additionCommutes = (omega + one) == (one + omega)
        val multiplicationCommutes = (omega * Ordinal.finite(2)) == (Ordinal.finite(2) * omega)
        return Pair(additionCommutes, multiplicationCommutes)
    }

    fun transfiniteSum(): Int {
        return TransfiniteRecursion.transfiniteRecursion(
            ordinal = Ordinal.finite(10),
            base = 0,
            successorCase = { _, prev -> prev + 1 },
            limitCase = { _, approx -> approx.last() }
        )
    }

    // ─────────────────────────────────────────────────────────
    // 10. INDEPENDÊNCIA DA HIPÓTESE DO CONTÍNUO (ANÁLOGO FINITO)
    // ─────────────────────────────────────────────────────────

    fun continuumHypothesisIndependence(): Pair<Boolean, Boolean> {
        return IndependenceDemo.summary()
    }

    // ─────────────────────────────────────────────────────────
    // 11. VERIFICAÇÃO DE AXIOMAS ZFC SOBRE MINI-MODELO
    // ─────────────────────────────────────────────────────────

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

    // ─────────────────────────────────────────────────────────
    // 12. CONSTRUÇÃO AXIOMÁTICA — √2 COMO SEQUÊNCIA DE CAUCHY
    // ─────────────────────────────────────────────────────────

    fun constructiveSqrt2(): Pair<String, Boolean> {
        val sqrt2 = ConstructedReal.squareRootOf(2)
        val isCauchy = sqrt2.isCauchyOnFinitePrefix(10)
        val approx = sqrt2.approximateRational(20)
        return Pair(approx.toString(), isCauchy)
    }

    // ─────────────────────────────────────────────────────────
    // 13. BIJEÇÃO CONSTRUTIVA ℕ ↔ ℚ — ENUMERABILIDADE
    // ─────────────────────────────────────────────────────────

    fun rationalEnumeration(): List<String> {
        return (0..9).map { i ->
            val n = NaturalNumber.of(i)
            val q = Countability.naturalToRational(n)
            "$i -> $q"
        }
    }

    // ─────────────────────────────────────────────────────────
    // 14. BELL NUMBERS — CONTAR PARTIÇÕES DE CONJUNTOS
    // ─────────────────────────────────────────────────────────

    fun bellNumbersDemo(): List<Pair<Int, String>> {
        return (0..5).map { n ->
            n to PartitionCalculus.bellNumber(n).toString()
        }
    }

    // ─────────────────────────────────────────────────────────
    // 15. POSETS E FILTROS — MODELO DE PREFERÊNCIAS
    // ─────────────────────────────────────────────────────────

    fun preferenceFilters(): Int {
        val options = setOf("A", "B", "C")
        val poset = Poset(options) { a, b ->
            a == b || (a == "A" && b != "A") || (a == "B" && b == "C")
        }
        return poset.filters().size
    }
}
