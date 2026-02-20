package mathsets.symbolic

/**
 * Symbolic expression tree.
 */
sealed interface Expression {
    data class Constant(val value: Double) : Expression
    data class Variable(val name: String) : Expression
    data class Add(val left: Expression, val right: Expression) : Expression
    data class Mul(val left: Expression, val right: Expression) : Expression
    data class Pow(val base: Expression, val exponent: Expression) : Expression
    data class Neg(val operand: Expression) : Expression
    data class FunctionCall(val name: String, val argument: Expression) : Expression
}

/**
 * Expression simplifier.
 */
object Simplifier {
    
    fun simplify(expr: Expression): Expression = when (expr) {
        is Expression.Constant -> expr
        is Expression.Variable -> expr
        is Expression.Neg -> simplifyNeg(expr.operand)
        is Expression.Add -> simplifyAdd(expr.left, expr.right)
        is Expression.Mul -> simplifyMul(expr.left, expr.right)
        is Expression.Pow -> simplifyPow(expr.base, expr.exponent)
        is Expression.FunctionCall -> expr
    }
    
    private fun simplifyNeg(operand: Expression): Expression {
        val simplified = simplify(operand)
        return when (simplified) {
            is Expression.Constant -> Expression.Constant(-simplified.value)
            is Expression.Neg -> simplified.operand // --x = x
            else -> Expression.Neg(simplified)
        }
    }
    
    private fun simplifyAdd(left: Expression, right: Expression): Expression {
        val l = simplify(left)
        val r = simplify(right)
        
        return when {
            l is Expression.Constant && l.value == 0.0 -> r
            r is Expression.Constant && r.value == 0.0 -> l
            l is Expression.Constant && r is Expression.Constant -> 
                Expression.Constant(l.value + r.value)
            else -> Expression.Add(l, r)
        }
    }
    
    private fun simplifyMul(left: Expression, right: Expression): Expression {
        val l = simplify(left)
        val r = simplify(right)
        
        return when {
            l is Expression.Constant && l.value == 0.0 -> Expression.Constant(0.0)
            r is Expression.Constant && r.value == 0.0 -> Expression.Constant(0.0)
            l is Expression.Constant && l.value == 1.0 -> r
            r is Expression.Constant && r.value == 1.0 -> l
            l is Expression.Constant && r is Expression.Constant -> 
                Expression.Constant(l.value * r.value)
            else -> Expression.Mul(l, r)
        }
    }
    
    private fun simplifyPow(base: Expression, exponent: Expression): Expression {
        val b = simplify(base)
        val e = simplify(exponent)
        
        return when {
            e is Expression.Constant && e.value == 0.0 -> Expression.Constant(1.0)
            e is Expression.Constant && e.value == 1.0 -> b
            b is Expression.Constant && b.value == 0.0 -> Expression.Constant(0.0)
            b is Expression.Constant && b.value == 1.0 -> Expression.Constant(1.0)
            else -> Expression.Pow(b, e)
        }
    }
}

/**
 * Symbolic differentiator.
 */
object Differentiator {
    
    fun differentiate(expr: Expression, variable: String): Expression {
        return when (expr) {
            is Expression.Constant -> Expression.Constant(0.0)
            is Expression.Variable -> 
                if (expr.name == variable) Expression.Constant(1.0) 
                else Expression.Constant(0.0)
            is Expression.Add -> 
                Expression.Add(differentiate(expr.left, variable), differentiate(expr.right, variable))
            is Expression.Mul -> 
                // Product rule: (fg)' = f'g + fg'
                Expression.Add(
                    Expression.Mul(differentiate(expr.left, variable), expr.right),
                    Expression.Mul(expr.left, differentiate(expr.right, variable))
                )
            is Expression.Pow -> 
                // Power rule for constant exponent: (x^n)' = n*x^(n-1)
                if (expr.exponent is Expression.Constant) {
                    val n = (expr.exponent as Expression.Constant).value
                    Expression.Mul(
                        Expression.Constant(n),
                        Expression.Pow(expr.base, Expression.Constant(n - 1))
                    )
                } else {
                    expr // Placeholder for general case
                }
            is Expression.Neg -> 
                Expression.Neg(differentiate(expr.operand, variable))
            is Expression.FunctionCall -> expr // Placeholder
        }
    }
}

/**
 * Expression parser (simple).
 */
object ExpressionParser {
    fun parse(input: String): Expression {
        // Placeholder - full implementation requires proper parser
        TODO("Full parser implementation needed")
    }
}
