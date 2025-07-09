package de.hu.kotlin.concurrency

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

// Demonstrates how to correctly handle exceptions so that is does not break coroutine cancellation.
fun main() {
    runBlocking {
        withTimeoutOrNull(2.seconds) { // Will cancel the coroutine after 2 seconds.
            while (true) {
                try {
                    doWork3()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: IllegalStateException) {
                    println("Oops: ${e::class} - ${e.message}")
                }
            }
        }
    }
}

suspend fun doWork3() {
    delay(500.milliseconds) // Will throw a TimeoutCancellationException if the job is no longer active.
    throw IllegalStateException("Just suppose a precondition is not met!")
}
