package de.hu.kotlin.concurrency

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun main() {
    println("Hello, Async World!")
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
            delay(delay)
        }
    }
}
