package mathsets.solver

/**
 * Propositional logic in CNF.
 */
data class CnfFormula(val clauses: List<List<Literals>>) {
    companion object {
        fun tautology(): CnfFormula = CnfFormula(listOf(listOf(Literals.True)))
        fun contradiction(): CnfFormula = CnfFormula(listOf()) // Empty set of clauses
    }
}

sealed interface Literals {
    data class Pos(val variable: String) : Literals
    data class Neg(val variable: String) : Literals
    object True : Literals
    object False : Literals
}

/**
 * DPLL SAT solver.
 */
object SatSolver {
    
    /**
     * Solves a CNF formula using DPLL algorithm.
     * Returns a satisfying assignment if one exists.
     */
    fun solve(cnf: CnfFormula): Map<String, Boolean>? {
        return dpll(cnf.clauses.toMutableList(), mutableMapOf())
    }
    
    private fun dpll(
        clauses: MutableList<List<Literals>>,
        assignment: MutableMap<String, Boolean>
    ): Map<String, Boolean>? {
        // Unit propagation
        val unitClauses = clauses.filter { it.size == 1 }
        for (unit in unitClauses) {
            when (val lit = unit[0]) {
                is Literals.Pos -> {
                    if (assignment[lit.variable] == false) return null
                    assignment[lit.variable] = true
                    if (!propagate(clauses, lit.variable, true)) return null
                }
                is Literals.Neg -> {
                    if (assignment[lit.variable] == true) return null
                    assignment[lit.variable] = false
                    if (!propagate(clauses, lit.variable, false)) return null
                }
                is Literals.False -> return null
                is Literals.True -> {} // Tautology, ignore
            }
        }
        
        // Check if all clauses satisfied
        if (clauses.isEmpty()) return assignment.toMap()
        
        // Check for empty clause (conflict)
        if (clauses.any { it.isEmpty() }) return null
        
        // Choose a variable
        val allVars = clauses.flatMap { clause ->
            clause.mapNotNull { lit ->
                when (lit) {
                    is Literals.Pos -> lit.variable
                    is Literals.Neg -> lit.variable
                    else -> null
                }
            }
        }.toSet()
        
        val varToBranch = allVars.firstOrNull { it !in assignment } ?: return assignment.toMap()
        
        // Try true first
        val assignmentTrue = assignment.toMutableMap()
        assignmentTrue[varToBranch] = true
        val clausesTrue = clauses.map { it.filterNot { lit ->
            when (lit) {
                is Literals.Pos -> lit.variable == varToBranch
                is Literals.Neg -> lit.variable == varToBranch && false
                else -> false
            }
        }.filterNot { lit ->
            when (lit) {
                is Literals.Neg -> lit.variable == varToBranch
                is Literals.Pos -> lit.variable == varToBranch && true
                else -> false
            }
        } }.toMutableList()
        
        val resultTrue = dpll(clausesTrue, assignmentTrue)
        if (resultTrue != null) return resultTrue
        
        // Try false
        val assignmentFalse = assignment.toMutableMap()
        assignmentFalse[varToBranch] = false
        val clausesFalse = clauses.map { it.filterNot { lit ->
            when (lit) {
                is Literals.Neg -> lit.variable == varToBranch
                is Literals.Pos -> lit.variable == varToBranch && false
                else -> false
            }
        }.filterNot { lit ->
            when (lit) {
                is Literals.Pos -> lit.variable == varToBranch
                is Literals.Neg -> lit.variable == varToBranch && true
                else -> false
            }
        } }.toMutableList()
        
        return dpll(clausesFalse, assignmentFalse)
    }
    
    private fun propagate(
        clauses: MutableList<List<Literals>>,
        variable: String,
        value: Boolean
    ): Boolean {
        // Remove clauses with literal that evaluates to true
        // Remove false literals from clauses
        val iterator = clauses.iterator()
        while (iterator.hasNext()) {
            val clause = iterator.next()
            val hasTrue = clause.any { lit ->
                when (lit) {
                    is Literals.Pos -> lit.variable == variable && value
                    is Literals.Neg -> lit.variable == variable && !value
                    is Literals.True -> true
                    is Literals.False -> false
                }
            }
            if (hasTrue) {
                iterator.remove()
                continue
            }
            
            // Remove false literals
            val newClause = clause.filterNot { lit ->
                when (lit) {
                    is Literals.Pos -> lit.variable == variable && !value
                    is Literals.Neg -> lit.variable == variable && value
                    is Literals.False -> true
                    is Literals.True -> false
                }
            }
            
            if (newClause.isEmpty()) return false // Conflict
            clauses[clauses.indexOf(clause)] = newClause
        }
        return true
    }
}

/**
 * Converts formulas to CNF (simplified).
 */
object CnfConverter {
    fun toCnf(formula: String): CnfFormula {
        // Placeholder - full implementation requires parsing and Tseitin transformation
        TODO("Full CNF conversion requires formula parser")
    }
}
