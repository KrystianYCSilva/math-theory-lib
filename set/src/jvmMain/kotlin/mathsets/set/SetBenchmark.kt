package mathsets.set

import java.util.concurrent.TimeUnit
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class SetBenchmark {

    private val base = mathSetOf(1..1_000)
    private val evens = base.filter { it % 2 == 0 }

    @Benchmark
    public fun unionFiniteSets(): MathSet<Int> = base union evens

    @Benchmark
    public fun intersectFiniteSets(): MathSet<Int> = base intersect evens
}

