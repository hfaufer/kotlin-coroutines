package de.hu.kotlin.concurrency

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

// Demonstrates that Kotlin's runCatching actually breaks coroutine cancellation.
fun main() {
    runBlocking {
        withTimeoutOrNull(2.seconds) { // Will cancel the coroutine after 2 seconds.
            while (true) {
                runCatching {
                    doWork2()
                }.onFailure { e ->
                    println("Oops: ${e::class} - ${e.message}")
                }
            }
        }
    }
}

private suspend fun doWork2() {
    delay(500.milliseconds) // Will throw a TimeoutCancellationException if the job is no longer active.
    throw IllegalStateException("Just suppose a precondition is not met!")
}
