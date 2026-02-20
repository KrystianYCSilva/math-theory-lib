# analysis

Base real analysis module for Wave 2.

## Includes

- `RealSequence` and `Limit` classification
- `RealSequence` support for `limsup` and `liminf` (finite-tail approximation)
- Pointwise numeric limits (`Limits.atPoint`)
- Sampled epsilon-delta continuity checks (`Continuity`)
- Symmetric finite-difference derivatives (`Differentiation`)
- Midpoint-rule Riemann integration (`RiemannIntegral`)
- Sampled Darboux lower/upper sums and integrability check (`RiemannIntegral.lowerSum/upperSum/isIntegrable`)
- Series helpers (`Series`) and `PowerSeries`, including absolute convergence and classical ratio/root/comparison tests
- Elementary functions via series (`exp`, `sin`, `cos`, `sinh`, `cosh`, `naturalLog`)
- Numerical checker for the Fundamental Theorem of Calculus
- `analysis.metric`: metric, normed, inner-product, Banach/Hilbert contracts and sample instances
