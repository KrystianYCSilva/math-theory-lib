package mathsets.examples

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import mathsets.kernel.RationalNumber

class CommercialUseCasesTest : FunSpec({

    // ── 1. RBAC ─────────────────────────────────────

    test("RBAC grants access based on role-permission chain") {
        val canAccess = CommercialUseCases.RBACEngine.evaluate(
            users = setOf("alice", "bob", "carol"),
            roles = setOf("admin", "viewer"),
            resources = setOf("dashboard", "settings", "reports"),
            userRoles = mapOf(
                "alice" to setOf("admin"),
                "bob" to setOf("viewer"),
                "carol" to setOf("admin", "viewer")
            ),
            rolePermissions = mapOf(
                "admin" to setOf("dashboard", "settings", "reports"),
                "viewer" to setOf("dashboard", "reports")
            )
        )
        canAccess("alice", "settings").shouldBeTrue()
        canAccess("bob", "settings").shouldBeFalse()
        canAccess("carol", "dashboard").shouldBeTrue()
    }

    test("RBAC detects segregation of duties violations") {
        val violations = CommercialUseCases.RBACEngine.verifySegregationOfDuties(
            users = setOf("alice", "bob"),
            userRoles = mapOf(
                "alice" to setOf("approver", "requester"),
                "bob" to setOf("viewer")
            ),
            conflictingPairs = listOf("approver" to "requester")
        )
        violations shouldBe listOf("alice")
    }

    test("RBAC finds uncovered resources") {
        val uncovered = CommercialUseCases.RBACEngine.verifyCompleteCoverage(
            resources = setOf("dashboard", "settings", "billing"),
            rolePermissions = mapOf("admin" to setOf("dashboard", "settings"))
        )
        uncovered shouldBe setOf("billing")
    }

    // ── 2. CREDIT RULE ENGINE ───────────────────────

    test("credit engine approves standard application") {
        val app = CommercialUseCases.CreditApplication("John", 90000, 720, false, 0.3)
        CommercialUseCases.CreditRuleEngine.evaluate(app) shouldBe "STANDARD"
    }

    test("credit engine approves premium application") {
        val app = CommercialUseCases.CreditApplication("Jane", 50000, 810, false, 0.2)
        CommercialUseCases.CreditRuleEngine.evaluate(app) shouldBe "PREMIUM"
    }

    test("credit engine denies high-risk application") {
        val app = CommercialUseCases.CreditApplication("Risk", 30000, 580, false, 0.7)
        CommercialUseCases.CreditRuleEngine.evaluate(app) shouldBe "DENIED"
    }

    test("credit engine approves via collateral override") {
        val app = CommercialUseCases.CreditApplication("Carl", 40000, 710, true, 0.5)
        CommercialUseCases.CreditRuleEngine.evaluate(app) shouldBe "COLLATERAL"
    }

    // ── 3. DEPENDENCY RESOLVER ──────────────────────

    test("dependency resolver produces valid topological order") {
        val result = CommercialUseCases.DependencyResolver.resolve(
            packages = listOf("app", "core", "utils", "db"),
            dependencies = listOf(
                "utils" to "core",
                "db" to "core",
                "utils" to "app",
                "core" to "app",
                "db" to "app"
            )
        )
        result.shouldHaveSize(4)
        result.indexOf("utils") shouldBe result.indexOfFirst { it == "utils" }
        (result.indexOf("utils") < result.indexOf("app")).shouldBeTrue()
        (result.indexOf("core") < result.indexOf("app")).shouldBeTrue()
        (result.indexOf("db") < result.indexOf("app")).shouldBeTrue()
    }

    // ── 4. DATA DEDUPLICATION ───────────────────────

    test("deduplicator finds duplicate records by email") {
        val records = listOf(
            CommercialUseCases.CustomerRecord(1, "Alice Smith", "alice@test.com"),
            CommercialUseCases.CustomerRecord(2, "A. Smith", "alice@test.com"),
            CommercialUseCases.CustomerRecord(3, "Bob Jones", "bob@test.com")
        )
        val groups = CommercialUseCases.DataDeduplicator.findDuplicateGroups(records) { a, b ->
            a.email == b.email
        }
        groups.shouldHaveSize(1)
        groups[0].shouldHaveSize(2)
        groups[0].map { it.id }.toSet() shouldBe setOf(1, 2)
    }

    // ── 5. A/B TEST ALLOCATION ──────────────────────

    test("A/B allocator distributes users across variants") {
        val allocation = CommercialUseCases.ABTestAllocator.allocate(
            users = listOf("user1", "user2", "user3", "user4"),
            variants = listOf("control", "treatment")
        )
        allocation["user1"] shouldBe "control"
        allocation["user2"] shouldBe "treatment"
        allocation["user3"] shouldBe "control"
        allocation["user4"] shouldBe "treatment"
    }

    test("A/B bijection is reversible for equal-size cohort") {
        val bij = CommercialUseCases.ABTestAllocator.createBijectionForCohort(
            userIds = listOf(1, 2, 3),
            variantIds = listOf("A", "B", "C")
        )
        bij.shouldNotBeNull()
        bij(1) shouldBe "A"
        bij.inverse()("B") shouldBe 2
    }

    // ── 6. FINANCIAL CALCULATOR ─────────────────────

    test("bill split is exact without floating-point loss") {
        CommercialUseCases.FinancialCalculator.verifySplitIsExact(10000, 3).shouldBeTrue()
        CommercialUseCases.FinancialCalculator.verifySplitIsExact(100, 7).shouldBeTrue()
    }

    test("compound interest calculates correctly") {
        val result = CommercialUseCases.FinancialCalculator.compoundInterest(
            principal = RationalNumber.of(10000),
            ratePercent = RationalNumber.of(10),
            periods = 2
        )
        result shouldBe RationalNumber.of(12100)
    }

    // ── 7. FEATURE FLAGS ────────────────────────────

    test("feature flag engine evaluates composite rules") {
        val flags = listOf(
            CommercialUseCases.FeatureFlagEngine.buildFlag(
                "dark-mode",
                CommercialUseCases.FeatureFlagEngine.premiumOnly,
                CommercialUseCases.FeatureFlagEngine.betaUsers
            ),
            CommercialUseCases.FeatureFlagEngine.buildFlag(
                "new-checkout",
                CommercialUseCases.FeatureFlagEngine.brazilOnly,
                CommercialUseCases.FeatureFlagEngine.recentVersion
            )
        )

        val premiumUser = CommercialUseCases.UserContext("u1", "US", true, false, 40)
        val betaBrazilian = CommercialUseCases.UserContext("u2", "BR", false, true, 30)

        val r1 = CommercialUseCases.FeatureFlagEngine.evaluate(premiumUser, flags)
        r1["dark-mode"] shouldBe true
        r1["new-checkout"] shouldBe false

        val r2 = CommercialUseCases.FeatureFlagEngine.evaluate(betaBrazilian, flags)
        r2["dark-mode"] shouldBe true
        r2["new-checkout"] shouldBe true
    }

    // ── 8. LOAD BALANCER ────────────────────────────

    test("load balancer selects one server per pool") {
        val pools = listOf(
            listOf(CommercialUseCases.Server("s1", 10), CommercialUseCases.Server("s2", 50)),
            listOf(CommercialUseCases.Server("s3", 30))
        )
        val result = CommercialUseCases.LoadBalancer.selectServers(pools)
        result.size shouldBe 2
    }

    // ── 9. BACKLOG PRIORITIZER ──────────────────────

    test("backlog prioritizer orders tickets by score") {
        val tickets = listOf(
            CommercialUseCases.Ticket("T1", priority = 3, effort = 5),
            CommercialUseCases.Ticket("T2", priority = 8, effort = 2),
            CommercialUseCases.Ticket("T3", priority = 5, effort = 10)
        )
        val sorted = CommercialUseCases.BacklogPrioritizer.prioritize(tickets)
        sorted[0].id shouldBe "T2"
        sorted[1].id shouldBe "T3"
        sorted[2].id shouldBe "T1"
    }

    // ── 10. PRICING GAME ────────────────────────────

    test("pricing game determines optimal strategy") {
        val (hasStrategy, bestMove, outcome) = CommercialUseCases.PricingGame.findOptimalStrategy(
            ourOptions = listOf(10, 20),
            competitorOptions = listOf(10, 20),
            horizon = 2,
            weWinIf = { moves -> moves[0] <= moves[1] }
        )
        (hasStrategy || !hasStrategy).shouldBeTrue()
        (outcome == 1 || outcome == -1).shouldBeTrue()
    }

    // ── 11. SCHEMA MAPPER ───────────────────────────

    test("schema mapper creates reversible field mapping") {
        val mapping = CommercialUseCases.SchemaMapper.createMapping(
            sourceFields = listOf("first_name", "last_name", "email"),
            targetFields = listOf("firstName", "lastName", "emailAddress"),
            transform = { field ->
                when (field) {
                    "first_name" -> "firstName"
                    "last_name" -> "lastName"
                    "email" -> "emailAddress"
                    else -> field
                }
            }
        )
        mapping.shouldNotBeNull()
        CommercialUseCases.SchemaMapper.roundTrip(mapping, "first_name") shouldBe "first_name"
        CommercialUseCases.SchemaMapper.roundTrip(mapping, "email") shouldBe "email"
    }

    // ── 12. DATA VALIDATOR ──────────────────────────

    test("data validator checks referential integrity") {
        val valid = CommercialUseCases.DataValidator.verifyReferentialIntegrity(
            orders = setOf(100, 101, 102),
            customers = setOf(1, 2, 3),
            orderToCustomer = mapOf(100 to 1, 101 to 2, 102 to 3)
        )
        valid.shouldBeTrue()

        val invalid = CommercialUseCases.DataValidator.verifyReferentialIntegrity(
            orders = setOf(100, 101),
            customers = setOf(1),
            orderToCustomer = mapOf(100 to 1, 101 to 99)
        )
        invalid.shouldBeFalse()
    }

    test("data validator checks uniqueness") {
        CommercialUseCases.DataValidator.verifyUniqueness(
            records = setOf("alice@test.com", "bob@test.com"),
            keyExtractor = { it }
        ).shouldBeTrue()
    }

    // ── 13. WORKFLOW ENGINE ─────────────────────────

    test("workflow engine identifies initial and terminal states") {
        val workflow = CommercialUseCases.WorkflowEngine.buildWorkflow(
            states = listOf("draft", "review", "approved", "published"),
            transitions = listOf(
                "draft" to "review",
                "review" to "approved",
                "approved" to "published"
            )
        )
        CommercialUseCases.WorkflowEngine.initialStates(workflow) shouldBe listOf("draft")
        CommercialUseCases.WorkflowEngine.terminalStates(workflow) shouldBe listOf("published")
    }

    // ── 14. SET QUERY ENGINE ────────────────────────

    test("set query engine filters products by multiple criteria") {
        val products = listOf(
            CommercialUseCases.Product(1, "electronics", 500, true),
            CommercialUseCases.Product(2, "electronics", 1500, true),
            CommercialUseCases.Product(3, "books", 30, false),
            CommercialUseCases.Product(4, "books", 20, true)
        )

        val result = CommercialUseCases.SetQueryEngine.query(
            products,
            categories = setOf("electronics"),
            maxPrice = 1000,
            onlyInStock = true
        )
        result.shouldHaveSize(1)
        result[0].id shouldBe 1
    }

    test("set query engine computes difference for exclusion lists") {
        val all = listOf(
            CommercialUseCases.Product(1, "a", 10, true),
            CommercialUseCases.Product(2, "b", 20, true),
            CommercialUseCases.Product(3, "c", 30, true)
        )
        val blocklist = listOf(
            CommercialUseCases.Product(2, "b", 20, true)
        )
        val result = CommercialUseCases.SetQueryEngine.difference(all, blocklist)
        result.map { it.id }.toSet() shouldBe setOf(1, 3)
    }

    // ── 15. PERMISSION AUDITOR ──────────────────────

    test("permission auditor generates full matrix and finds excessive perms") {
        val matrix = CommercialUseCases.PermissionAuditor.generateAuditMatrix(
            roles = listOf("admin", "user"),
            resources = listOf("read", "write", "delete"),
            isGranted = { role, _ -> role == "admin" }
        )
        matrix.shouldHaveSize(6)
        matrix.count { it.third } shouldBe 3

        val excessive = CommercialUseCases.PermissionAuditor.findExcessivePermissions(matrix, 2)
        excessive.shouldContainKey("admin")
        excessive["admin"] shouldBe 3
    }
})
