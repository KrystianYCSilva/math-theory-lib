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

/**
 * Commercial and enterprise use cases demonstrating how mathematical abstractions from
 * the library can be applied to real-world software engineering problems.
 *
 * Covers RBAC access control, credit scoring, dependency resolution, data deduplication,
 * A/B testing, financial calculations, feature flags, load balancing, backlog prioritization,
 * pricing game theory, schema mapping, data validation, workflow engines, set-based queries,
 * and permission auditing.
 */
object CommercialUseCases {

    /**
     * Represents an access request with its grant/deny result.
     *
     * @property user The user requesting access.
     * @property resource The resource being accessed.
     * @property granted Whether access was granted.
     */
    data class AccessRequest(val user: String, val resource: String, val granted: Boolean)

    /**
     * Role-Based Access Control engine that evaluates permissions using set-theoretic relations.
     */
    object RBACEngine {
        /**
         * Builds an access evaluation function from user-role and role-permission mappings.
         *
         * @param users The set of all users.
         * @param roles The set of all roles.
         * @param resources The set of all resources.
         * @param userRoles A mapping from each user to their assigned roles.
         * @param rolePermissions A mapping from each role to its permitted resources.
         * @return A function that takes (user, resource) and returns whether access is granted.
         */
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

        /**
         * Checks for segregation-of-duties violations: users holding conflicting role pairs.
         *
         * @param users The set of all users.
         * @param userRoles A mapping from each user to their assigned roles.
         * @param conflictingPairs Pairs of roles that should not be held simultaneously.
         * @return A list of users who violate at least one segregation-of-duties constraint.
         */
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

        /**
         * Finds resources not covered by any role's permissions.
         *
         * @param resources The set of all resources.
         * @param rolePermissions A mapping from each role to its permitted resources.
         * @return The set of uncovered resources.
         */
        fun verifyCompleteCoverage(
            resources: Set<String>,
            rolePermissions: Map<String, Set<String>>
        ): Set<String> {
            val covered = rolePermissions.values.flatten().toSet()
            return resources - covered
        }
    }

    /**
     * A credit application with financial attributes for rule evaluation.
     *
     * @property name The applicant's name.
     * @property income The annual income.
     * @property score The credit score.
     * @property hasCollateral Whether the applicant has collateral.
     * @property debtRatio The debt-to-income ratio.
     */
    data class CreditApplication(
        val name: String,
        val income: Int,
        val score: Int,
        val hasCollateral: Boolean,
        val debtRatio: Double
    )

    /**
     * Credit approval rule engine using composed predicates.
     *
     * Combines income, score, debt ratio, and collateral checks into approval tiers
     * (PREMIUM, STANDARD, COLLATERAL, DENIED) using logical predicate composition.
     */
    object CreditRuleEngine {
        private val highIncome: Predicate<CreditApplication> = { it.income >= 80000 }
        private val goodScore: Predicate<CreditApplication> = { it.score >= 700 }
        private val excellentScore: Predicate<CreditApplication> = { it.score >= 800 }
        private val lowDebt: Predicate<CreditApplication> = { it.debtRatio < 0.4 }
        private val hasCollateral: Predicate<CreditApplication> = { it.hasCollateral }

        /** Predicate for standard approval: high income AND good score AND low debt. */
        val standardApproval: Predicate<CreditApplication> =
            (highIncome and goodScore) and lowDebt

        /** Predicate for premium approval: excellent score AND low debt. */
        val premiumApproval: Predicate<CreditApplication> =
            excellentScore and lowDebt

        /** Predicate for collateral-backed approval: has collateral AND good score. */
        val collateralOverride: Predicate<CreditApplication> =
            hasCollateral and goodScore

        /** Predicate that matches any approval tier. */
        val anyApproval: Predicate<CreditApplication> =
            standardApproval or premiumApproval or collateralOverride

        /** Predicate for denial: the negation of any approval. */
        val denial: Predicate<CreditApplication> = anyApproval.not()

        /**
         * Evaluates a credit application and returns the approval tier.
         *
         * @param app The credit application to evaluate.
         * @return One of "PREMIUM", "STANDARD", "COLLATERAL", or "DENIED".
         */
        fun evaluate(app: CreditApplication): String = when {
            premiumApproval(app) -> "PREMIUM"
            standardApproval(app) -> "STANDARD"
            collateralOverride(app) -> "COLLATERAL"
            else -> "DENIED"
        }
    }

    /**
     * Topological dependency resolver using partial orders.
     *
     * Resolves build dependencies by constructing a transitive closure of the dependency
     * graph and iteratively extracting minimal elements.
     */
    object DependencyResolver {
        /**
         * Resolves package dependencies into a topological build order.
         *
         * @param packages The list of package names.
         * @param dependencies Pairs (a, b) meaning "a must come before b".
         * @return A topologically sorted list of package names.
         */
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

    /**
     * A customer record for deduplication.
     *
     * @property id The unique record ID.
     * @property name The customer name.
     * @property email The customer email.
     */
    data class CustomerRecord(val id: Int, val name: String, val email: String)

    /**
     * Data deduplication engine using equivalence relations.
     *
     * Groups records into duplicate clusters based on a user-defined similarity predicate,
     * modeled as an equivalence relation and partitioned into equivalence classes.
     */
    object DataDeduplicator {
        /**
         * Finds groups of duplicate records based on the given similarity predicate.
         *
         * @param records The list of customer records.
         * @param areSame A predicate determining if two records are duplicates.
         * @return A list of duplicate groups (groups with more than one record).
         */
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

    /**
     * A/B test allocator using bijections for deterministic, reversible user assignment.
     */
    object ABTestAllocator {
        /**
         * Allocates users to test variants in a round-robin fashion.
         *
         * @param U The user type.
         * @param users The list of users to allocate.
         * @param variants The list of variant names.
         * @return A map from each user to their assigned variant.
         */
        fun <U> allocate(
            users: List<U>,
            variants: List<String>
        ): Map<U, String> {
            require(users.isNotEmpty() && variants.isNotEmpty())
            return users.mapIndexed { index, user ->
                user to variants[index % variants.size]
            }.toMap()
        }

        /**
         * Creates a bijection between user IDs and variant IDs for a same-size cohort.
         *
         * @param userIds The list of user IDs.
         * @param variantIds The list of variant IDs (must have same size as [userIds]).
         * @return A [Bijection] from user IDs to variant IDs, or `null` if sizes differ.
         */
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

    /**
     * Financial calculator using exact rational arithmetic to avoid floating-point errors.
     */
    object FinancialCalculator {
        /**
         * Splits a bill evenly among parties using exact rational division.
         *
         * @param totalCents The total amount in cents.
         * @param parties The number of parties (must be positive).
         * @return A list of equal rational shares.
         */
        fun splitBill(totalCents: Int, parties: Int): List<RationalNumber> {
            require(parties > 0)
            val total = RationalNumber.of(totalCents, 1)
            val divisor = RationalNumber.of(parties, 1)
            val share = total / divisor
            return List(parties) { share }
        }

        /**
         * Computes compound interest using exact rational arithmetic.
         *
         * @param principal The initial amount.
         * @param ratePercent The interest rate as a percentage (e.g., 5 for 5%).
         * @param periods The number of compounding periods.
         * @return The final amount after compounding.
         */
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

        /**
         * Verifies that splitting a bill and summing the shares yields exactly the original total.
         *
         * @param totalCents The total amount in cents.
         * @param parties The number of parties.
         * @return `true` if the sum of shares equals the total (always true with exact rationals).
         */
        fun verifySplitIsExact(totalCents: Int, parties: Int): Boolean {
            val shares = splitBill(totalCents, parties)
            val sum = shares.fold(RationalNumber.ZERO) { acc, r -> acc + r }
            return sum == RationalNumber.of(totalCents, 1)
        }
    }

    /**
     * User context for feature flag evaluation.
     *
     * @property userId The user's unique identifier.
     * @property country The user's country code.
     * @property isPremium Whether the user has a premium subscription.
     * @property betaOptIn Whether the user opted into beta features.
     * @property appVersion The user's application version number.
     */
    data class UserContext(
        val userId: String,
        val country: String,
        val isPremium: Boolean,
        val betaOptIn: Boolean,
        val appVersion: Int
    )

    /**
     * Feature flag engine using composed predicates for safe feature rollout.
     */
    object FeatureFlagEngine {
        /**
         * Builds a named feature flag from one or more predicate rules (OR-composed).
         *
         * @param name The feature flag name.
         * @param rules The predicate rules; the flag is enabled if any rule matches.
         * @return A pair of (name, combined predicate).
         */
        fun buildFlag(
            name: String,
            vararg rules: Predicate<UserContext>
        ): Pair<String, Predicate<UserContext>> {
            val combined = rules.reduce { acc, rule -> acc or rule }
            return name to combined
        }

        /**
         * Evaluates all feature flags for a given user context.
         *
         * @param context The user context to evaluate against.
         * @param flags The list of feature flags.
         * @return A map from flag name to whether it is enabled.
         */
        fun evaluate(
            context: UserContext,
            flags: List<Pair<String, Predicate<UserContext>>>
        ): Map<String, Boolean> {
            return flags.associate { (name, predicate) -> name to predicate(context) }
        }

        /** Predicate matching premium users only. */
        val premiumOnly: Predicate<UserContext> = { it.isPremium }
        /** Predicate matching users who opted into beta. */
        val betaUsers: Predicate<UserContext> = { it.betaOptIn }
        /** Predicate matching users in Brazil. */
        val brazilOnly: Predicate<UserContext> = { it.country == "BR" }
        /** Predicate matching users on app version 50 or later. */
        val recentVersion: Predicate<UserContext> = { it.appVersion >= 50 }
    }

    /**
     * A server with an identifier and current load metric.
     *
     * @property id The server identifier.
     * @property load The current load (lower is better).
     */
    data class Server(val id: String, val load: Int)

    /**
     * Load balancer using the Axiom of Choice to select one server from each pool.
     */
    object LoadBalancer {
        /**
         * Selects one server from each pool using a choice function.
         *
         * @param pools A list of server pools (each pool is a non-empty list of servers).
         * @return A map from pool labels ("pool-0", "pool-1", ...) to the selected server.
         */
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

    /**
     * A backlog ticket with priority and effort attributes.
     *
     * @property id The ticket identifier.
     * @property priority The priority level (higher is more important).
     * @property effort The estimated effort (lower is preferred).
     */
    data class Ticket(val id: String, val priority: Int, val effort: Int) {
        /**
         * Computes a composite score for prioritization (higher is better).
         *
         * @return The priority score.
         */
        fun score(): Int = priority * 100 - effort
    }

    /**
     * Backlog prioritizer using total orders (well-orderings) for sprint planning.
     */
    object BacklogPrioritizer {
        /**
         * Prioritizes tickets by constructing a total order based on their composite score.
         *
         * @param tickets The list of tickets to prioritize.
         * @return The tickets sorted in priority order (highest score first).
         */
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

    /**
     * Pricing strategy engine using Gale-Stewart game theory for competitive price decisions.
     */
    object PricingGame {
        /**
         * Finds the optimal pricing strategy by modeling competition as a Gale-Stewart game.
         *
         * @param ourOptions Our available price points.
         * @param competitorOptions The competitor's available price points.
         * @param horizon The number of pricing rounds.
         * @param weWinIf A payoff function that returns `true` if we win given the move history.
         * @return A [Triple] of (we have a winning strategy, best opening move or null, minimax outcome).
         */
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

    /**
     * Schema mapper using bijections for reversible data model transformations.
     */
    object SchemaMapper {
        /**
         * Creates a bijective mapping between source and target schema fields.
         *
         * @param A The source field type.
         * @param B The target field type.
         * @param sourceFields The list of source fields.
         * @param targetFields The list of target fields (must have same size).
         * @param transform The transformation function from source to target fields.
         * @return A [Bijection] between fields, or `null` if sizes differ or bijection fails.
         */
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

        /**
         * Performs a round-trip: maps a value forward and then back via the inverse.
         *
         * @param A The source type.
         * @param B The target type.
         * @param mapping The bijective mapping.
         * @param value The value to round-trip.
         * @return The original value after forward and inverse mapping.
         */
        fun <A, B> roundTrip(mapping: Bijection<A, B>, value: A): A {
            return mapping.inverse()(mapping(value))
        }
    }

    /**
     * Data validation engine using first-order logic for complex dataset invariants.
     */
    object DataValidator {
        /**
         * Verifies referential integrity: every order references a valid customer.
         *
         * @param orders The set of order IDs.
         * @param customers The set of customer IDs.
         * @param orderToCustomer A mapping from order ID to customer ID.
         * @return `true` if every order's customer exists in the customer set.
         */
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

        /**
         * Verifies that a key extractor produces unique keys for all records.
         *
         * @param records The set of records.
         * @param keyExtractor A function extracting the key from each record.
         * @return `true` if all extracted keys are unique.
         */
        fun verifyUniqueness(
            records: Set<String>,
            keyExtractor: (String) -> String
        ): Boolean {
            val keys = records.map(keyExtractor)
            return keys.toSet().size == keys.size
        }
    }

    /**
     * Workflow engine modeling state transitions as a partial order.
     */
    object WorkflowEngine {
        /**
         * Builds a workflow as a partial order from states and transitions.
         *
         * @param states The list of workflow state names.
         * @param transitions Pairs (from, to) defining valid state transitions.
         * @return A [PartialOrder] representing the workflow.
         */
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

        /**
         * Checks whether a state transition is valid in the workflow.
         *
         * @param workflow The workflow partial order.
         * @param from The source state.
         * @param to The target state.
         * @return `true` if the transition from [from] to [to] is valid.
         */
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

        /**
         * Returns the initial (minimal) states of the workflow.
         *
         * @param workflow The workflow partial order.
         * @return The list of initial states.
         */
        fun initialStates(workflow: PartialOrder<String>): List<String> {
            return workflow.minimals().elements().toList()
        }

        /**
         * Returns the terminal (maximal) states of the workflow.
         *
         * @param workflow The workflow partial order.
         * @return The list of terminal states.
         */
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

    /**
     * A product with category, price, and stock attributes.
     *
     * @property id The product ID.
     * @property category The product category.
     * @property price The product price.
     * @property inStock Whether the product is in stock.
     */
    data class Product(val id: Int, val category: String, val price: Int, val inStock: Boolean)

    /**
     * Set-based query engine using set algebra (union, intersection, difference) for filtering.
     */
    object SetQueryEngine {
        /**
         * Queries products by optional category, price, and stock filters using set operations.
         *
         * @param products The list of all products.
         * @param categories Optional set of categories to filter by.
         * @param maxPrice Optional maximum price filter.
         * @param onlyInStock If `true`, only return in-stock products.
         * @return The filtered list of products.
         */
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

        /**
         * Computes the intersection of two product lists.
         *
         * @param list1 The first product list.
         * @param list2 The second product list.
         * @return Products present in both lists.
         */
        fun intersection(
            list1: List<Product>,
            list2: List<Product>
        ): List<Product> {
            val set1 = mathSetOf(list1)
            val set2 = mathSetOf(list2)
            return (set1 intersect set2).elements().toList()
        }

        /**
         * Computes the set difference: all products minus excluded ones.
         *
         * @param all The full product list.
         * @param exclude Products to exclude.
         * @return Products in [all] but not in [exclude].
         */
        fun difference(
            all: List<Product>,
            exclude: List<Product>
        ): List<Product> {
            val setAll = mathSetOf(all)
            val setExclude = mathSetOf(exclude)
            return (setAll minus setExclude).elements().toList()
        }
    }

    /**
     * Permission auditor using Cartesian products for comprehensive role-resource review.
     */
    object PermissionAuditor {
        /**
         * Generates a full audit matrix of all role-resource permission combinations.
         *
         * @param roles The list of roles to audit.
         * @param resources The list of resources to audit.
         * @param isGranted A function that returns whether a role has access to a resource.
         * @return A list of (role, resource, granted) triples.
         */
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

        /**
         * Finds roles with an excessive number of granted permissions.
         *
         * @param matrix The audit matrix generated by [generateAuditMatrix].
         * @param maxPerRole The maximum allowed permissions per role.
         * @return A map from role name to permission count for roles exceeding the limit.
         */
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
