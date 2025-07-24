package de.hu.kotlin.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import java.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Demonstrates executing a "bare" coroutine with GlobalScope.launch().
 * However, this low-level approach creates a coroutine that is not governed
 * by structured concurrency. Coroutines launched on the global scope can’t
 * be automatically cancelled and aren’t aware of any life cycle. This means
 * it’s very easy to introduce resource leaks, or to still execute a coroutine
 * who's work is no longer needed.
 * One use case for GlobalScope.launch() is to launch a top-level background
 * processes that must stay active for the whole lifetime of an application.
 */
fun main() {
    val job = GlobalScope.launch {
        println("Hello")
        delay(1.seconds)
        println(", world!")
    }
    sleep(Duration.ofSeconds(2))
}

