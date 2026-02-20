package mathsets.symbolic

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SymbolicCoreTest : FunSpec({
    test("simplifier reduces constant expressions") {
        val expr = Expression.Add(
            Expression.Constant(2.0),
            Expression.Constant(3.0)
        )
        
        val result = Simplifier.simplify(expr)
        result shouldBe Expression.Constant(5.0)
    }
    
    test("simplifier handles zero and one identities") {
        val x = Expression.Variable("x")
        
        // x + 0 = x
        Simplifier.simplify(Expression.Add(x, Expression.Constant(0.0))) shouldBe x
        
        // x * 1 = x
        Simplifier.simplify(Expression.Mul(x, Expression.Constant(1.0))) shouldBe x
        
        // x * 0 = 0
        Simplifier.simplify(Expression.Mul(x, Expression.Constant(0.0))) shouldBe Expression.Constant(0.0)
    }
    
    test("differentiator computes derivative of x^2") {
        val xSquared = Expression.Pow(
            Expression.Variable("x"),
            Expression.Constant(2.0)
        )
        
        val derivative = Differentiator.differentiate(xSquared, "x")
        
        // d/dx(x²) = 2x
        val expected = Expression.Mul(
            Expression.Constant(2.0),
            Expression.Variable("x")
        )
        
        Simplifier.simplify(derivative) shouldBe Simplifier.simplify(expected)
    }
    
    test("differentiator computes derivative of sum") {
        val x = Expression.Variable("x")
        val expr = Expression.Add(x, Expression.Pow(x, Expression.Constant(2.0)))
        
        val derivative = Differentiator.differentiate(expr, "x")
        
        // d/dx(x + x²) = 1 + 2x
        (derivative is Expression.Add) shouldBe true
    }
})
