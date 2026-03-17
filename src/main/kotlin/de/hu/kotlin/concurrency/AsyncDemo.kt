package de.hu.kotlin.concurrency

import de.hu.kotlin.coroutines.Log
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

/*
 * Demonstrates that coroutines built with `async` run interleaved in thread "main".
 *
 * When you replace `delay` with `Thread.sleep` in AsyncRandomSum.compute(), the coroutines
 * will be executed one after another, because `Thread.sleep` blocks the single thread
 * of execution.
 *
 * After replacing `delay` with `Thread.sleep`, you can execute the outer coroutine
 * with `runBlocking(Dispatchers.IO)` to see that now the two computations run in parallel
 * again, because this time they are executed in two different (platform) threads.
 * When running on JVM 21 or above, you can also use `runBlocking(Dispatchers.VT)` to
 * see the same effect. But now it uses a new virtual thread for every async computation.
 */
fun main() {
    println("Hello, Async World!")
    Log.info("Starting 2 async computations...")
    //runBlocking(Dispatchers.IO) {
//    runBlocking(Dispatchers.VT) {
    runBlocking { // Runs the coroutines in the blocked thread.
        val counter1: Deferred<Int> = async { AsyncRandomSum("Sum-1", 1500.milliseconds, steps = 10).compute() }
        val counter2: Deferred<Int> = async { AsyncRandomSum("Sum-2", 1000.milliseconds, steps = 15).compute() }
        Log.info("Launched async jobs ${counter1.key} and ${counter2.key}.")
        val result = counter1.await() + counter2.await()
        Log.info("Result is $result.")
    }
}

class AsyncRandomSum(
    private val name: String,
    private val delay: Duration,
    private val steps: Int,
) {

    suspend fun compute(): Int {
        var sum = 0
        (1..steps).forEach { i ->
            Log.info("$name step $i")
            sum += Random.nextInt(10)
            // Try replacing `delay` with `Thread.sleep` to see the difference in execution order.
            delay(delay)
//             Thread.sleep(delay.toJavaDuration())
        }
        Log.info("$name: Result is $sum")
        return sum
    }
}
