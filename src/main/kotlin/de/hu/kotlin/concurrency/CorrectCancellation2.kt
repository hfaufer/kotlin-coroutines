package de.hu.kotlin.concurrency

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

// Demonstrates how to correctly use Kotlin's runCatching so that is does not break coroutine cancellation.
fun main() {
    runBlocking {
        withTimeoutOrNull(2.seconds) { // Will cancel the coroutine after 2 seconds.
            while (true) {
                runCatching {
                    doWork4()
                }.onFailure { e ->
                    when (e) {
                        is CancellationException -> throw e
                        is IllegalStateException -> println("Oops: ${e::class} - ${e.message}")
                        else -> throw e
                    }
                }
            }
        }
    }
}

private suspend fun doWork4() {
    delay(500.milliseconds) // Will throw a TimeoutCancellationException if the job is no longer active.
    throw IllegalStateException("Just suppose a precondition is not met!")
}
