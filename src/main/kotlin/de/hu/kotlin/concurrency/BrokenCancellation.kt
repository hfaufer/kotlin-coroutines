package de.hu.kotlin.concurrency

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

// Demonstrates that catching an IllegalStateException in a coroutine can actually break cancellation.
fun main() {
    runBlocking {
        withTimeoutOrNull(2.seconds) { // Will cancel the coroutine after 2 seconds.
            while (true) {
                try {
                    doWork()
                } catch (e: IllegalStateException) {
                    println("Oops: ${e::class} - ${e.message}")
                }
            }
        }
    }
}

suspend fun doWork() {
    delay(500.milliseconds) // Will throw a TimeoutCancellationException if the job is no longer active.
    throw IllegalStateException("Just suppose a precondition is not met!")
}
