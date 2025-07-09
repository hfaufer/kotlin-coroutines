package de.hu.kotlin.concurrency

import de.hu.kotlin.coroutines.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis

// Demonstrates that Dispatchers.Default or Dispatchers.IO should be used for parallel computations.
// Dispatchers.Default is optimal for CPU-bound tasks because its pool size is equals to the number
// of available CPU cores. That is, you will not distribute the computation over more threads than
// there are CPU cores.
fun main() {
    println("Hello, Behnke Benchmark!")
    val elapsedTime = measureTimeMillis {
        val result = Behnke(Dispatchers.Default).compute(10, 100_000_000_000L)
        Log.info("Result is $result.")
    }
    Log.info("Computation completed in $elapsedTime ms")
}

class Behnke(private val dispatcher: CoroutineDispatcher) {

    fun compute(workerCount: Int, n: Long): Double =
        runBlocking(dispatcher) {
            (1..workerCount).map { workerId ->
                async {
                    val min = (workerId - 1) * n / workerCount + 1
                    val max = workerId * n / workerCount
                    Log.info("Worker $workerId: Computing sum from $min to $max")
                    val localSum = (min..max).sumOf { sqrt(1.0 / it) }
                    Log.info("Worker $workerId: Local Sum is $localSum")
                    localSum
                }
            }.sumOf { it.await() }
        }
}
