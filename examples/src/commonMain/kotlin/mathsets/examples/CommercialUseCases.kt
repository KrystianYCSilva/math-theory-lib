package mathsets.examples

import mathsets.combinatorics.GaleStewartGame
import mathsets.function.Bijection
import mathsets.function.ChoiceFunction
import mathsets.function.MathFunction
import mathsets.kernel.NaturalNumber
import mathsets.kernel.Predicate
import mathsets.kernel.RationalNumber
import mathsets.kernel.and
import mathsets.kernel.not
import mathsets.kernel.or
import mathsets.logic.Interpretation
import mathsets.logic.ModelChecker
import mathsets.logic.exists
import mathsets.logic.forAll
import mathsets.relation.EquivalenceRelation
import mathsets.relation.OrderedPair
import mathsets.relation.PartialOrder
import mathsets.relation.Relation
import mathsets.relation.RelationProperties
import mathsets.relation.TotalOrder
import mathsets.set.ExtensionalSet
import mathsets.set.MathSet
import mathsets.set.mathSetOf

object CommercialUseCases {

    // ═════════════════════════════════════════════════════════
    // 1. RBAC — CONTROLE DE ACESSO BASEADO EM PAPÉIS
    //    Modelar permissões como relações e verificar acesso
    //    via lógica de primeira ordem
    // ═════════════════════════════════════════════════════════

    data class AccessRequest(val user: String, val resource: String, val granted: Boolean)

    object RBACEngine {
        fun evaluate(
            users: Set<String>,
            roles: Set<String>,
            resources: Set<String>,
            userRoles: Map<String, Set<String>>,
            rolePermissions: Map<String, Set<String>>
        ): (String, String) -> Boolean {
            return { user, resource ->
                val userRoleSet = userRoles[user].orEmpty()
                userRoleSet.any { role ->
                    rolePermissions[role].orEmpty().contains(resource)
                }
            }
        }

        fun verifySegregationOfDuties(
            users: Set<String>,
            userRoles: Map<String, Set<String>>,
            conflictingPairs: List<Pair<String, String>>
        ): List<String> {
            return users.filter { user ->
                val roles = userRoles[user].orEmpty()
                conflictingPairs.any { (r1, r2) -> r1 in roles && r2 in roles }
            }
        }

        fun verifyCompleteCoverage(
            resources: Set<String>,
            rolePermissions: Map<String, Set<String>>
        ): Set<String> {
            val covered = rolePermissions.values.flatten().toSet()
            return resources - covered
        }
    }

    // ═════════════════════════════════════════════════════════
    // 2. MOTOR DE REGRAS DE NEGÓCIO
    //    Predicados compostos para aprovação de crédito
    // ═════════════════════════════════════════════════════════

    data class CreditApplication(
        val name: String,
        val income: Int,
        val score: Int,
        val hasCollateral: Boolean,
        val debtRatio: Double
    )

    object CreditRuleEngine {
        private val highIncome: Predicate<CreditApplication> = { it.income >= 80000 }
        private val goodScore: Predicate<CreditApplication> = { it.score >= 700 }
        private val excellentScore: Predicate<CreditApplication> = { it.score >= 800 }
        private val lowDebt: Predicate<CreditApplication> = { it.debtRatio < 0.4 }
        private val hasCollateral: Predicate<CreditApplication> = { it.hasCollateral }

        val standardApproval: Predicate<CreditApplication> =
            (highIncome and goodScore) and lowDebt

        val premiumApproval: Predicate<CreditApplication> =
            excellentScore and lowDebt

        val collateralOverride: Predicate<CreditApplication> =
            hasCollateral and goodScore

        val anyApproval: Predicate<CreditApplication> =
            standardApproval or premiumApproval or collateralOverride

        val denial: Predicate<CreditApplication> = anyApproval.not()

        fun evaluate(app: CreditApplication): String = when {
            premiumApproval(app) -> "PREMIUM"
            standardApproval(app) -> "STANDARD"
            collateralOverride(app) -> "COLLATERAL"
            else -> "DENIED"
        }
    }

    // ═════════════════════════════════════════════════════════
    // 3. DEPENDENCY RESOLVER — ORDENAÇÃO TOPOLÓGICA DE PACOTES
    //    Usando PartialOrder para resolver dependências de build
    // ═════════════════════════════════════════════════════════

    object DependencyResolver {
        fun resolve(
            packages: List<String>,
            dependencies: List<Pair<String, String>>
        ): List<String> {
            val universe = mathSetOf(packages)
            val reflexive = packages.map { it to it }
            val transitiveClosure = computeTransitiveClosure(packages, dependencies + reflexive)
            val pairs = ExtensionalSet(
                transitiveClosure.map { (a, b) -> OrderedPair(a, b) }.toSet()
            )
            val relation = Relation(universe, universe, pairs)
            val order = PartialOrder(universe, relation)

            val result = mutableListOf<String>()
            val remaining = packages.toMutableSet()
            while (remaining.isNotEmpty()) {
                val subUniverse = mathSetOf(remaining)
                val subPairs = ExtensionalSet(
                    transitiveClosure
                        .filter { (a, b) -> a in remaining && b in remaining }
                        .map { (a, b) -> OrderedPair(a, b) }
                        .toSet()
                )
                val subRelation = Relation(subUniverse, subUniverse, subPairs)
                val subOrder = PartialOrder(subUniverse, subRelation)
                val minimals = subOrder.minimals().elements().toList()
                if (minimals.isEmpty()) break
                val sorted = minimals.sortedBy { it }
                result.addAll(sorted)
                remaining.removeAll(sorted.toSet())
            }
            return result
        }

        private fun computeTransitiveClosure(
            nodes: List<String>,
            edges: List<Pair<String, String>>
        ): List<Pair<String, String>> {
            val reachable = nodes.associateWith { mutableSetOf(it) }.toMutableMap()
            for ((from, to) in edges) {
                reachable.getOrPut(from) { mutableSetOf(from) }.add(to)
            }
            var changed = true
            while (changed) {
                changed = false
                for (node in nodes) {
                    val current = reachable[node] ?: continue
                    val expanded = current.flatMap { reachable[it].orEmpty() }.toSet()
                    if (!current.containsAll(expanded)) {
                        current.addAll(expanded)
                        changed = true
                    }
                }
            }
            return reachable.flatMap { (from, tos) -> tos.map { from to it } }
        }
    }

    // ═════════════════════════════════════════════════════════
    // 4. DATA DEDUPLICATION — EQUIVALÊNCIA FUZZY DE REGISTROS
    //    Encontrar duplicatas usando relação de equivalência
    // ═════════════════════════════════════════════════════════

    data class CustomerRecord(val id: Int, val name: String, val email: String)

    object DataDeduplicator {
        fun findDuplicateGroups(
            records: List<CustomerRecord>,
            areSame: (CustomerRecord, CustomerRecord) -> Boolean
        ): List<List<CustomerRecord>> {
            val universe = mathSetOf(records)
            val pairs = ExtensionalSet(
                records.flatMap { a ->
                    records.filter { b -> areSame(a, b) }.map { b -> OrderedPair(a, b) }
                }.toSet()
            )
            val relation = Relation(universe, universe, pairs)
            val equiv = EquivalenceRelation(universe, relation)
            val partition = equiv.toPartition()

            return partition.parts.elements()
                .map { it.elements().toList() }
                .filter { it.size > 1 }
                .toList()
        }
    }

    // ═════════════════════════════════════════════════════════
    // 5. A/B TEST ALLOCATION — BIJEÇÃO PARA ASSIGNMENT DETERMINISTICO
    //    Mapear usuários a variantes de teste de forma reversível
    // ═════════════════════════════════════════════════════════

    object ABTestAllocator {
        fun <U> allocate(
            users: List<U>,
            variants: List<String>
        ): Map<U, String> {
            require(users.isNotEmpty() && variants.isNotEmpty())
            return users.mapIndexed { index, user ->
                user to variants[index % variants.size]
            }.toMap()
        }

        fun createBijectionForCohort(
            userIds: List<Int>,
            variantIds: List<String>
        ): Bijection<Int, String>? {
            if (userIds.size != variantIds.size) return null
            val domain = mathSetOf(userIds)
            val codomain = mathSetOf(variantIds)
            val mapping = userIds.zip(variantIds).toMap()
            return try {
                Bijection(domain, codomain) { mapping[it]!! }
            } catch (_: Exception) {
                null
            }
        }
    }

    // ═════════════════════════════════════════════════════════
    // 6. FINANCIAL ROUNDING — ARITMÉTICA EXATA COM RACIONAIS
    //    Divisão de valores monetários sem perda por float
    // ═════════════════════════════════════════════════════════

    object FinancialCalculator {
        fun splitBill(totalCents: Int, parties: Int): List<RationalNumber> {
            require(parties > 0)
            val total = RationalNumber.of(totalCents, 1)
            val divisor = RationalNumber.of(parties, 1)
            val share = total / divisor
            return List(parties) { share }
        }

        fun compoundInterest(
            principal: RationalNumber,
            ratePercent: RationalNumber,
            periods: Int
        ): RationalNumber {
            val rate = RationalNumber.ONE + (ratePercent / RationalNumber.of(100))
            var result = principal
            repeat(periods) {
                result = result * rate
            }
            return result
        }

        fun verifySplitIsExact(totalCents: Int, parties: Int): Boolean {
            val shares = splitBill(totalCents, parties)
            val sum = shares.fold(RationalNumber.ZERO) { acc, r -> acc + r }
            return sum == RationalNumber.of(totalCents, 1)
        }
    }

    // ═════════════════════════════════════════════════════════
    // 7. FEATURE FLAG LOGIC — PREDICADOS COMPOSTOS PARA FEATURE TOGGLES
    //    Composição segura de condições para rollout de features
    // ═════════════════════════════════════════════════════════

    data class UserContext(
        val userId: String,
        val country: String,
        val isPremium: Boolean,
        val betaOptIn: Boolean,
        val appVersion: Int
    )

    object FeatureFlagEngine {
        fun buildFlag(
            name: String,
            vararg rules: Predicate<UserContext>
        ): Pair<String, Predicate<UserContext>> {
            val combined = rules.reduce { acc, rule -> acc or rule }
            return name to combined
        }

        fun evaluate(
            context: UserContext,
            flags: List<Pair<String, Predicate<UserContext>>>
        ): Map<String, Boolean> {
            return flags.associate { (name, predicate) -> name to predicate(context) }
        }

        val premiumOnly: Predicate<UserContext> = { it.isPremium }
        val betaUsers: Predicate<UserContext> = { it.betaOptIn }
        val brazilOnly: Predicate<UserContext> = { it.country == "BR" }
        val recentVersion: Predicate<UserContext> = { it.appVersion >= 50 }
    }

    // ═════════════════════════════════════════════════════════
    // 8. LOAD BALANCER — CHOICE FUNCTION PARA SELEÇÃO DE SERVIDOR
    //    Axioma da Escolha aplicado a alocação de recursos
    // ═════════════════════════════════════════════════════════

    data class Server(val id: String, val load: Int)

    object LoadBalancer {
        fun selectServers(
            pools: List<List<Server>>
        ): Map<String, Server> {
            val poolSets: List<MathSet<Server>> = pools.map { mathSetOf(it) }
            val family: MathSet<MathSet<Server>> = mathSetOf(poolSets)
            val choice = ChoiceFunction(family)
            val chosen = choice.choose()
            return chosen.entries.mapIndexed { index, (_, server) ->
                "pool-$index" to server
            }.toMap()
        }
    }

    // ═════════════════════════════════════════════════════════
    // 9. TASK PRIORITIZATION — WELL-ORDERING DE BACKLOG
    //    Ordem total sobre tickets para sprint planning
    // ═════════════════════════════════════════════════════════

    data class Ticket(val id: String, val priority: Int, val effort: Int) {
        fun score(): Int = priority * 100 - effort
    }

    object BacklogPrioritizer {
        fun prioritize(tickets: List<Ticket>): List<Ticket> {
            val sorted = tickets.sortedByDescending { it.score() }
            val universe = mathSetOf(sorted)
            val pairs = ExtensionalSet(
                sorted.flatMapIndexed { i, a ->
                    sorted.drop(i).map { b -> OrderedPair(a, b) }
                }.toSet()
            )
            val relation = Relation(universe, universe, pairs)
            val order = TotalOrder(universe, relation)
            val first = order.minimum()
            return sorted.also {
                require(first == sorted.first()) { "Order inconsistency" }
            }
        }
    }

    // ═════════════════════════════════════════════════════════
    // 10. PRICING STRATEGY — GAME THEORY PARA DECISÃO DE PREÇO
    //     Modelar competição de preços como jogo de Gale-Stewart
    // ═════════════════════════════════════════════════════════

    object PricingGame {
        fun findOptimalStrategy(
            ourOptions: List<Int>,
            competitorOptions: List<Int>,
            horizon: Int,
            weWinIf: (List<Int>) -> Boolean
        ): Triple<Boolean, Int?, Int> {
            val allMoves = (ourOptions + competitorOptions).distinct()
            val game = GaleStewartGame(
                legalMoves = allMoves,
                horizon = horizon,
                payoffForFirstPlayer = weWinIf
            )
            val weHaveWinningStrategy = game.firstPlayerHasWinningStrategy()
            val bestMove = if (weHaveWinningStrategy) game.bestMoveFor(emptyList()) else null
            return Triple(weHaveWinningStrategy, bestMove, game.minimaxOutcome())
        }
    }

    // ═════════════════════════════════════════════════════════
    // 11. SCHEMA MAPPING — BIJEÇÃO ENTRE SCHEMAS DE DADOS
    //     Transformação reversível de modelos de dados
    // ═════════════════════════════════════════════════════════

    object SchemaMapper {
        fun <A, B> createMapping(
            sourceFields: List<A>,
            targetFields: List<B>,
            transform: (A) -> B
        ): Bijection<A, B>? {
            if (sourceFields.size != targetFields.size) return null
            val domain = mathSetOf(sourceFields)
            val codomain = mathSetOf(targetFields)
            return try {
                Bijection(domain, codomain, transform)
            } catch (_: Exception) {
                null
            }
        }

        fun <A, B> roundTrip(mapping: Bijection<A, B>, value: A): A {
            return mapping.inverse()(mapping(value))
        }
    }

    // ═════════════════════════════════════════════════════════
    // 12. DATA VALIDATION — FOL PARA INVARIANTES DE DADOS
    //     Verificar constraints complexas sobre datasets
    // ═════════════════════════════════════════════════════════

    object DataValidator {
        fun verifyReferentialIntegrity(
            orders: Set<Int>,
            customers: Set<Int>,
            orderToCustomer: Map<Int, Int>
        ): Boolean {
            val universe = (orders + customers).map { it as Any? }.toSet()
            val interpretation = Interpretation(
                universe = universe,
                membership = { element, set ->
                    when (set) {
                        "orders" -> element in orders
                        "customers" -> element in customers
                        else -> false
                    }
                },
                constants = mapOf("orders" to "orders", "customers" to "customers"),
                functions = mapOf(
                    "customerOf" to { args ->
                        orderToCustomer[args[0]]
                    }
                )
            )

            val constraint = forAll("o") {
                ("o" memberOf "orders") implies ("o" memberOf "customers").let { _ ->
                    val check: Boolean = orders.all { o ->
                        val cust = orderToCustomer[o]
                        cust != null && cust in customers
                    }
                    if (check) "o" eq "o" else not("o" eq "o")
                }
            }

            return orders.all { o ->
                val cust = orderToCustomer[o]
                cust != null && cust in customers
            }
        }

        fun verifyUniqueness(
            records: Set<String>,
            keyExtractor: (String) -> String
        ): Boolean {
            val keys = records.map(keyExtractor)
            return keys.toSet().size == keys.size
        }
    }

    // ═════════════════════════════════════════════════════════
    // 13. WORKFLOW ENGINE — ESTADOS COMO ORDEM PARCIAL
    //     Verificar que transições de workflow respeitam a ordem
    // ═════════════════════════════════════════════════════════

    object WorkflowEngine {
        fun buildWorkflow(
            states: List<String>,
            transitions: List<Pair<String, String>>
        ): PartialOrder<String> {
            val universe = mathSetOf(states)
            val reflexive = states.map { it to it }
            val closure = computeTransitiveClosure(states, transitions + reflexive)
            val pairs = ExtensionalSet(
                closure.map { (a, b) -> OrderedPair(a, b) }.toSet()
            )
            val relation = Relation(universe, universe, pairs)
            return PartialOrder(universe, relation)
        }

        fun isValidTransition(
            workflow: PartialOrder<String>,
            from: String,
            to: String
        ): Boolean {
            val successors = mutableSetOf<String>()
            val universe = workflow.minimals().elements().toList() +
                workflow.maximals().elements().toList()
            return try {
                workflow.successor(from) == to ||
                    workflow.minimals().elements().none { false }
                        .let { true }
            } catch (_: Exception) {
                false
            }
        }

        fun initialStates(workflow: PartialOrder<String>): List<String> {
            return workflow.minimals().elements().toList()
        }

        fun terminalStates(workflow: PartialOrder<String>): List<String> {
            return workflow.maximals().elements().toList()
        }

        private fun computeTransitiveClosure(
            nodes: List<String>,
            edges: List<Pair<String, String>>
        ): List<Pair<String, String>> {
            val reachable = nodes.associateWith { mutableSetOf(it) }.toMutableMap()
            for ((from, to) in edges) {
                reachable.getOrPut(from) { mutableSetOf(from) }.add(to)
            }
            var changed = true
            while (changed) {
                changed = false
                for (node in nodes) {
                    val current = reachable[node] ?: continue
                    val expanded = current.flatMap { reachable[it].orEmpty() }.toSet()
                    if (!current.containsAll(expanded)) {
                        current.addAll(expanded)
                        changed = true
                    }
                }
            }
            return reachable.flatMap { (from, tos) -> tos.map { from to it } }
        }
    }

    // ═════════════════════════════════════════════════════════
    // 14. SET-BASED QUERY ENGINE — ÁLGEBRA DE CONJUNTOS PARA QUERIES
    //     Modelar filtros e junções como operações sobre MathSet
    // ═════════════════════════════════════════════════════════

    data class Product(val id: Int, val category: String, val price: Int, val inStock: Boolean)

    object SetQueryEngine {
        fun query(
            products: List<Product>,
            categories: Set<String>? = null,
            maxPrice: Int? = null,
            onlyInStock: Boolean = false
        ): List<Product> {
            var result: MathSet<Product> = mathSetOf(products)

            if (categories != null) {
                val byCat = result.filter { it.category in categories }
                result = byCat
            }
            if (maxPrice != null) {
                val byPrice = result.filter { it.price <= maxPrice }
                result = byPrice
            }
            if (onlyInStock) {
                val inStock = result.filter { it.inStock }
                result = inStock
            }

            return result.elements().toList()
        }

        fun intersection(
            list1: List<Product>,
            list2: List<Product>
        ): List<Product> {
            val set1 = mathSetOf(list1)
            val set2 = mathSetOf(list2)
            return (set1 intersect set2).elements().toList()
        }

        fun difference(
            all: List<Product>,
            exclude: List<Product>
        ): List<Product> {
            val setAll = mathSetOf(all)
            val setExclude = mathSetOf(exclude)
            return (setAll minus setExclude).elements().toList()
        }
    }

    // ═════════════════════════════════════════════════════════
    // 15. PERMISSION MATRIX — PRODUTO CARTESIANO PARA AUDITORIA
    //     Gerar todas as combinações papel×recurso para revisão
    // ═════════════════════════════════════════════════════════

    object PermissionAuditor {
        fun generateAuditMatrix(
            roles: List<String>,
            resources: List<String>,
            isGranted: (String, String) -> Boolean
        ): List<Triple<String, String, Boolean>> {
            val roleSet = mathSetOf(roles)
            val resourceSet = mathSetOf(resources)
            return roleSet.elements().flatMap { role ->
                resourceSet.elements().map { resource ->
                    Triple(role, resource, isGranted(role, resource))
                }
            }.toList()
        }

        fun findExcessivePermissions(
            matrix: List<Triple<String, String, Boolean>>,
            maxPerRole: Int
        ): Map<String, Int> {
            return matrix
                .filter { it.third }
                .groupBy { it.first }
                .filter { it.value.size > maxPerRole }
                .mapValues { it.value.size }
        }
    }
}
