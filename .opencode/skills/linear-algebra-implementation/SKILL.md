---
name: linear-algebra-implementation
description: >-
  Implements matrix operations, vector spaces, and linear transformations using immutable structures.
  Use when: solving linear systems, computing eigenvalues, performing matrix decompositions, or modeling vector spaces.
---

# Skill: Linear Algebra Implementation

This skill provides patterns for implementing linear algebra in Kotlin with mathematical rigor. It covers matrix operations, vector spaces, linear transformations, and advanced decompositions using type-safe, immutable structures.

## How to Represent Matrices

### Immutable Matrix Representation

```kotlin
/**
 * An m × n matrix over field F (typically ℚ or ℝ).
 * Stored as a 2D array for performance, but exposed immutably.
 */
class Matrix(
    val rows: Int,
    val cols: Int,
    private val data: Array<Array<RationalNumber>>
) {
    init {
        require(rows > 0 && cols > 0) { "Matrix dimensions must be positive" }
        require(data.size == rows && data.all { it.size == cols }) {
            "Data dimensions must match declared rows×cols"
        }
    }
    
    /** Access element at (i,j) - 0-indexed */
    operator fun get(i: Int, j: Int): RationalNumber = data[i][j]
    
    /** Matrix is square? */
    val isSquare: Boolean get() = rows == cols
    
    /** Identity matrix Iₙ */
    companion object {
        fun identity(n: Int): Matrix {
            val data = Array(n) { i ->
                Array(n) { j ->
                    if (i == j) RationalNumber.one else RationalNumber.zero
                }
            }
            return Matrix(n, n, data)
        }
        
        /** Zero matrix 0ₘ×ₙ */
        fun zero(rows: Int, cols: Int): Matrix {
            val data = Array(rows) { Array(cols) { RationalNumber.zero } }
            return Matrix(rows, cols, data)
        }
    }
}
```

### Vector as Column Matrix

```kotlin
/**
 * A vector in Fⁿ represented as n×1 column matrix.
 */
typealias Vector = Matrix

fun vector(components: List<RationalNumber>): Vector {
    val data = Array(components.size) { i ->
        arrayOf(components[i])
    }
    return Matrix(components.size, 1, data)
}

/** Dot product: u · v = uᵀv */
infix fun Vector.dot(other: Vector): RationalNumber {
    require(this.rows == other.rows) { "Vectors must have same dimension" }
    return (0 until this.rows).sumOf { i ->
        this[i, 0].times(other[i, 0]).toDouble()
    }.let { RationalNumber(it.toLong()) } // Simplified
}
```

## How to Implement Matrix Operations

### Matrix Addition and Scalar Multiplication

```kotlin
/** Matrix addition: (A + B)[i,j] = A[i,j] + B[i,j] */
operator fun Matrix.plus(other: Matrix): Matrix {
    require(this.rows == other.rows && this.cols == other.cols) {
        "Matrix dimensions must match for addition"
    }
    
    val result = Array(rows) { i ->
        Array(cols) { j ->
            this[i, j] + other[i, j]
        }
    }
    
    return Matrix(rows, cols, result)
}

/** Scalar multiplication: (cA)[i,j] = c · A[i,j] */
operator fun Matrix.times(scalar: RationalNumber): Matrix {
    val result = Array(rows) { i ->
        Array(cols) { j ->
            this[i, j] * scalar
        }
    }
    
    return Matrix(rows, cols, result)
}
```

### Matrix Multiplication

```kotlin
/**
 * Matrix multiplication: (AB)[i,j] = Σₖ A[i,k] · B[k,j]
 * Complexity: O(mnp) for (m×n) × (n×p)
 */
operator fun Matrix.times(other: Matrix): Matrix {
    require(this.cols == other.rows) {
        "Matrix A cols (${this.cols}) must equal matrix B rows (${other.rows})"
    }
    
    val m = this.rows
    val n = this.cols
    val p = other.cols
    
    val result = Array(m) { i ->
        Array(p) { j ->
            var sum = RationalNumber.zero
            for (k in 0 until n) {
                sum += this[i, k] * other[k, j]
            }
            sum
        }
    }
    
    return Matrix(m, p, result)
}
```

### Transpose

```kotlin
/** Transpose: (Aᵀ)[i,j] = A[j,i] */
fun Matrix.transpose(): Matrix {
    val result = Array(cols) { i ->
        Array(rows) { j ->
            this[j, i]
        }
    }
    
    return Matrix(cols, rows, result)
}
```

## How to Solve Linear Systems

### Gaussian Elimination

Transform matrix to row echelon form:

```kotlin
/**
 * Gaussian elimination to row echelon form.
 * Returns: (REF matrix, permutation vector for pivoting)
 */
fun Matrix.gaussianElimination(): Pair<Matrix, List<Int>> {
    val augmented = this.data.map { it.copyOf() }.toMutableList()
    val n = rows
    val m = cols
    val permutation = (0 until n).toList().toMutableList()
    
    var pivotRow = 0
    
    for (col in 0 until m) {
        // Find pivot (row with largest absolute value in current column)
        var maxRow = pivotRow
        for (row in (pivotRow + 1) until n) {
            if (augmented[row][col].toDouble().absoluteValue > 
                augmented[maxRow][col].toDouble().absoluteValue) {
                maxRow = row
            }
        }
        
        if (augmented[maxRow][col] == RationalNumber.zero) {
            continue // No pivot in this column
        }
        
        // Swap rows
        val temp = augmented[pivotRow]
        augmented[pivotRow] = augmented[maxRow]
        augmented[maxRow] = temp
        val permTemp = permutation[pivotRow]
        permutation[pivotRow] = permutation[maxRow]
        permutation[maxRow] = permTemp
        
        // Eliminate below
        for (row in (pivotRow + 1) until n) {
            val factor = augmented[row][col] / augmented[pivotRow][col]
            for (j in col until m) {
                augmented[row][j] -= factor * augmented[pivotRow][j]
            }
        }
        
        pivotRow++
    }
    
    val resultData = augmented.map { it.copyOfArray() }.toTypedArray()
    return Matrix(n, m, resultData) to permutation.toList()
}
```

### Solve Ax = b via Back Substitution

```kotlin
/**
 * Solve linear system Ax = b where A is upper triangular.
 * Uses back substitution.
 */
fun solveUpperTriangular(A: Matrix, b: Vector): Vector {
    require(A.isSquare && A.rows == b.rows) {
        "A must be square and compatible with b"
    }
    
    val n = A.rows
    val x = Array(n) { RationalNumber.zero }
    
    for (i in (n - 1) downTo 0) {
        var sum = RationalNumber.zero
        for (j in (i + 1) until n) {
            sum += A[i, j] * x[j]
        }
        x[i] = (b[i, 0] - sum) / A[i, i]
    }
    
    return vector(x.toList())
}

/**
 * Solve general Ax = b using Gaussian elimination with partial pivoting.
 */
fun Matrix.solve(b: Vector): Vector {
    // Form augmented matrix [A | b]
    val augmented = augmentWith(b)
    
    // Reduce to REF
    val (ref, _) = augmented.gaussianElimination()
    
    // Extract transformed b and solve
    val n = this.rows
    val bTransformed = vector((0 until n).map { i -> ref[i, n] })
    val AUpper = Matrix(n, n, Array(n) { i -> ref[i].copyOfRange(0, n) })
    
    return solveUpperTriangular(AUpper, bTransformed)
}

fun Matrix.augmentWith(b: Vector): Matrix {
    require(this.rows == b.rows) { "Vector b must have same rows as matrix" }
    
    val newCols = this.cols + 1
    val newData = Array(rows) { i ->
        Array(newCols) { j ->
            if (j < cols) this[i, j] else b[i, 0]
        }
    }
    
    return Matrix(rows, newCols, newData)
}
```

## How to Compute Advanced Decompositions

### LU Decomposition

Factor A = LU where L is lower triangular, U is upper triangular:

```kotlin
/**
 * LU decomposition with partial pivoting: PA = LU
 * @return (L, U, P) where L is lower unitriangular, U is upper triangular
 */
fun Matrix.luDecomposition(): Triple<Matrix, Matrix, Matrix> {
    require(this.isSquare) { "LU decomposition requires square matrix" }
    
    val n = this.rows
    val L = Matrix.identity(n).data.map { it.copyOf() }.toMutableList()
    val U = this.data.map { it.copyOf() }.toMutableList()
    val P = Matrix.identity(n).data.map { it.copyOf() }.toMutableList()
    
    for (k in 0 until n) {
        // Find pivot
        var maxRow = k
        for (i in (k + 1) until n) {
            if (U[i][k].toDouble().absoluteValue > 
                U[maxRow][k].toDouble().absoluteValue) {
                maxRow = i
            }
        }
        
        // Swap rows in U and P
        val tempU = U[k]; U[k] = U[maxRow]; U[maxRow] = tempU
        val tempP = P[k]; P[k] = P[maxRow]; P[maxRow] = tempP
        
        // Swap rows in L (only the part already computed)
        for (j in 0 until k) {
            val tempL = L[k][j]; L[k][j] = L[maxRow][j]; L[maxRow][j] = tempL
        }
        
        // Eliminate
        for (i in (k + 1) until n) {
            val factor = U[i][k] / U[k][k]
            L[i][k] = factor.toDouble() // Store multiplier in L
            
            for (j in k until n) {
                U[i][j] -= factor * U[k][j]
            }
        }
    }
    
    return Triple(
        Matrix(n, n, L.map { it.copyOfArray() }.toTypedArray()),
        Matrix(n, n, U.map { it.copyOfArray() }.toTypedArray()),
        Matrix(n, n, P.map { it.copyOfArray() }.toTypedArray())
    )
}
```

## Best Practices

1. **Validate dimensions** - Always check compatibility before operations
2. **Use exact arithmetic** - Prefer `RationalNumber` over `Double` for correctness
3. **Implement pivoting** - Partial pivoting prevents numerical instability
4. **Cache decompositions** - Store LU/QR/Eigen results for repeated solves
5. **Document complexity** - Note O(n³) for most matrix operations

## Common Operations Reference

| Operation | Complexity | Method |
|-----------|------------|--------|
| Matrix Multiply | O(n³) | `A * B` |
| Determinant | O(n³) | `det()` via LU |
| Inverse | O(n³) | `inverse()` via Gauss-Jordan |
| Linear Solve | O(n³) | `A.solve(b)` |
| Eigenvalues | O(n³·iterations) | QR iteration |
| SVD | O(n³·iterations) | Golub-Kahan |

## References

1. **Source:** `linalg/src/commonMain/kotlin/mathsets/linalg/` from mathsets-kt
2. **Linear Algebra:** Strang, G. *Introduction to Linear Algebra*. 5th ed. Wellesley-Cambridge, 2016.
3. **Matrix Computations:** Golub, G.H.; Van Loan, C.F. *Matrix Computations*. 4th ed. Johns Hopkins, 2013.
4. **Exact Arithmetic:** `RationalNumber` from `kernel/` module for precise computation
