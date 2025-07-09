package de.hu.kotlin.concurrency

import de.hu.kotlin.coroutines.Log
import kotlinx.coroutines.*
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis

/**
 * Demonstrates that Dispatchers.Default or Dispatchers.IO should be used for parallel computations.
 * Dispatchers.Default is optimal for CPU-bound tasks because its pool size is equal to the number
 * of available CPU cores. That is, you will not distribute the computation over more threads than
 * there are CPU cores.
 * The two variants of the compute() method show how to implement the same computation as:
 * 1) a normal function that starts the initial coroutine with the given dispatcher,
 * 2) a suspend function that witches to the Default dispatcher.
 */
fun main() {
    println("Hello, Behnke Benchmark!")
    val elapsedTime = measureTimeMillis {
        //        val result = Behnke().compute(Dispatchers.Default, 10, 100_000_000_000L)
        val result = runBlocking {
            Behnke().compute(10, 100_000_000_000L)
        }
        Log.info("Result is $result.")
    }
    Log.info("Computation completed in $elapsedTime ms")
}

class Behnke() {

    fun compute(dispatcher: CoroutineDispatcher, workerCount: Int, n: Long): Double =
        runBlocking(dispatcher) {
            series(workerCount, n)
        }

    suspend fun compute(workerCount: Int, n: Long): Double =
        withContext(Dispatchers.Default) {
            series(workerCount, n)
        }

    private suspend fun CoroutineScope.series(workerCount: Int, n: Long): Double =
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
