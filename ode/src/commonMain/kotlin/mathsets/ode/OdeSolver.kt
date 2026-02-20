package mathsets.ode

/**
 * Ordinary Differential Equation: dy/dt = f(t, y)
 */
interface ODE {
    fun f(t: Double, y: Double): Double
}

/**
 * Euler's method for numerical integration.
 */
object EulerMethod {
    
    /**
     * Solves ODE using Euler's method.
     * 
     * @param ode The ODE to solve.
     * @param y0 Initial condition y(t0).
     * @param t0 Initial time.
     * @param tFinal Final time.
     * @param stepSize Step size h.
     * @return List of (t, y) pairs.
     */
    fun solve(ode: ODE, y0: Double, t0: Double, tFinal: Double, stepSize: Double): List<Pair<Double, Double>> {
        val result = mutableListOf<Pair<Double, Double>>()
        var t = t0
        var y = y0
        
        result += t to y
        
        while (t < tFinal) {
            val slope = ode.f(t, y)
            y += stepSize * slope
            t += stepSize
            result += t to y
        }
        
        return result
    }
}

/**
 * Runge-Kutta 4th order method.
 */
object RungeKutta4 {
    
    /**
     * Solves ODE using RK4 method.
     * 
     * @param ode The ODE to solve.
     * @param y0 Initial condition y(t0).
     * @param t0 Initial time.
     * @param tFinal Final time.
     * @param stepSize Step size h.
     * @return List of (t, y) pairs.
     */
    fun solve(ode: ODE, y0: Double, t0: Double, tFinal: Double, stepSize: Double): List<Pair<Double, Double>> {
        val result = mutableListOf<Pair<Double, Double>>()
        var t = t0
        var y = y0
        
        result += t to y
        
        while (t < tFinal) {
            val k1 = ode.f(t, y)
            val k2 = ode.f(t + stepSize/2, y + stepSize*k1/2)
            val k3 = ode.f(t + stepSize/2, y + stepSize*k2/2)
            val k4 = ode.f(t + stepSize, y + stepSize*k3)
            
            y += stepSize * (k1 + 2*k2 + 2*k3 + k4) / 6
            t += stepSize
            result += t to y
        }
        
        return result
    }
}

/**
 * System of ODEs: dy_i/dt = f_i(t, y_1, ..., y_n)
 */
interface ODESystem {
    val dimension: Int
    fun f(t: Double, y: List<Double>): List<Double>
}

/**
 * RK4 for systems of ODEs.
 */
object RungeKutta4System {
    
    fun solve(system: ODESystem, y0: List<Double>, t0: Double, tFinal: Double, stepSize: Double): List<Pair<Double, List<Double>>> {
        require(y0.size == system.dimension) { "Initial condition dimension mismatch." }
        
        val result = mutableListOf<Pair<Double, List<Double>>>()
        var t = t0
        var y = y0
        
        result += t to y
        
        while (t < tFinal) {
            val k1 = system.f(t, y)
            val y2 = y.zip(k1).map { (yi, k1i) -> yi + stepSize * k1i / 2 }
            val k2 = system.f(t + stepSize/2, y2)
            val y3 = y.zip(k2).map { (yi, k2i) -> yi + stepSize * k2i / 2 }
            val k3 = system.f(t + stepSize/2, y3)
            val y4 = y.zip(k3).map { (yi, k3i) -> yi + stepSize * k3i }
            val k4 = system.f(t + stepSize, y4)
            
            y = y.zip(k1).zip(k2).zip(k3).zip(k4).map { quadruple ->
                val yi = quadruple.first.first.first.first
                val k1i = quadruple.first.first.first.second
                val k2i = quadruple.first.first.second
                val k3i = quadruple.first.second
                val k4i = quadruple.second
                yi + stepSize * (k1i + 2*k2i + 2*k3i + k4i) / 6
            }
            t += stepSize
            result += t to y
        }
        
        return result
    }
}
