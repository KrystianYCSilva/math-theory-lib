package mathsets.kernel

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
public class KernelBenchmark {

    private val a: NaturalNumber = NaturalNumber.of(123456789L)
    private val b: NaturalNumber = NaturalNumber.of(987654321L)

    @Benchmark
    public fun addNaturalNumbers(): NaturalNumber = a + b

    @Benchmark
    public fun multiplyNaturalNumbers(): NaturalNumber = a * b
}

