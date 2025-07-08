package de.hu.kotlin.concurrency

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

/*
 * Demonstrates that coroutines built with `async` run interleaved in a single thread.
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
    println("Hello, Async World!")
    // Try replacing `delay` with `Thread.sleep` to see the difference in execution order.
    // runBlocking(Dispatchers.IO) {
    //runBlocking(Dispatchers.VT) {
    runBlocking {
        val counter1 = async { AsyncCounter("Counter-1", 1500.milliseconds, end = 10).run() }
        val counter2 = async { AsyncCounter("Counter-2", 1000.milliseconds, end = 15).run() }
        counter1.await()
        counter2.await()
    }
}

class AsyncCounter(
    private val name: String,
    private val delay: Duration,
    private val start: Int = 1,
    private val end: Int = 12,
) {

    suspend fun run() {
        (start..end).forEach { i ->
            val threadName = Thread.currentThread().name
            println("[$threadName] $name: $i")
            // Try replacing `delay` with `Thread.sleep` to see the difference in execution order.
            delay(delay)
            // Thread.sleep(delay.toJavaDuration())
        }
    }
}

private val Dispatchers.VT: CoroutineDispatcher
    get() {
        // In a real application, you would use Dispatchers.VT from kotlinx.coroutines.
        return Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()
    }
