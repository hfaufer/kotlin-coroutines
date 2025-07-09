package de.hu.kotlin.concurrency

import de.hu.kotlin.coroutines.Log
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/*
 * Demonstrates that coroutines built with `launch` run interleaved in a single thread.
 *
 * When you replace `delay` with `Thread.sleep` in AsyncCounter.run(), the coroutines
 * will be executed one after another, because `Thread.sleep` blocks the single thread
 * of execution.
 *
 * After replacing `delay` with `Thread.sleep`, you can execute the outer coroutine
 * with `runBlocking(Dispatchers.IO)` to see that now the two counters run in parallel
 * again, because this time they are executed in two different (platform) threads.
 * When running on JVM 21 or above, you can also use `runBlocking(Dispatchers.VT)` to
 * see the same effect. But now it uses virtual threads instead of platform threads.
 */
fun main() {
    println("Hello, Launch World!")
    //    runBlocking(Dispatchers.IO) {
    //    runBlocking(Dispatchers.VT) {
    runBlocking {
        val job1: Job = launch { AsyncCounter("Counter-1", 1500.milliseconds, end = 10).run() }
        val job2: Job = launch { AsyncCounter("Counter-2", 1000.milliseconds, end = 15).run() }
        Log.info("Launched jobs ${job1.key} and ${job2.key}.")
    }
}

class AsyncCounter(
    private val name: String,
    private val delay: Duration,
    private val start: Int = 1,
    private val end: Int,
) {

    suspend fun run() {
        (start..end).forEach { i ->
            Log.info("$name: $i")
            // Try replacing `delay` with `Thread.sleep` to see the difference in execution order.
            delay(delay)
            // Thread.sleep(delay.toJavaDuration())
        }
    }
}
