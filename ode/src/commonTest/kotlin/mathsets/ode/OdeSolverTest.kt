package mathsets.ode

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.math.E
import kotlin.math.abs

class OdeSolverTest : FunSpec({
    test("euler method solves dy/dt = y with y(0)=1") {
        val ode = object : ODE {
            override fun f(t: Double, y: Double): Double = y
        }
        
        val result = EulerMethod.solve(ode, y0 = 1.0, t0 = 0.0, tFinal = 1.0, stepSize = 0.01)
        
        // Exact solution: y = e^t, so y(1) ≈ 2.718
        val finalY = result.last().second
        val diff = abs(finalY - E)
        (diff < 0.15) shouldBe true // Euler has ~O(h) error
    }
    
    test("runge kutta 4 is more accurate than euler") {
        val ode = object : ODE {
            override fun f(t: Double, y: Double): Double = y
        }
        
        val eulerResult = EulerMethod.solve(ode, y0 = 1.0, t0 = 0.0, tFinal = 1.0, stepSize = 0.01)
        val rk4Result = RungeKutta4.solve(ode, y0 = 1.0, t0 = 0.0, tFinal = 1.0, stepSize = 0.01)
        
        val eulerError = abs(eulerResult.last().second - E)
        val rk4Error = abs(rk4Result.last().second - E)
        
        val isMoreAccurate = rk4Error < eulerError
        isMoreAccurate shouldBe true
    }
    
    test("ode system solves coupled equations") {
        // Simple harmonic oscillator: dx/dt = v, dv/dt = -x
        val system = object : ODESystem {
            override val dimension = 2
            override fun f(t: Double, y: List<Double>): List<Double> {
                val x = y[0]
                val v = y[1]
                return listOf(v, -x)
            }
        }
        
        val result = RungeKutta4System.solve(
            system,
            y0 = listOf(1.0, 0.0), // x(0)=1, v(0)=0
            t0 = 0.0,
            tFinal = 3.14159 / 2, // π/2
            stepSize = 0.01
        )
        
        // At t=π/2, x should be ~0, v should be ~-1
        val finalState = result.last().second
        val diffX = abs(finalState[0])
        val diffV = abs(finalState[1] - (-1.0))
        (diffX < 0.1) shouldBe true
        (diffV < 0.1) shouldBe true
    }
})
