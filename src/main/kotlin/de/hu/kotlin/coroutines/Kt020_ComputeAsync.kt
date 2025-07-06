package de.hu.kotlin.coroutines

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private val ASYNC_TASK_DELAY: Duration = 3.seconds

fun main(): Unit = runBlocking {
    Log.info("Starting computation ...")
    val elapsedMillis = measureTimeMillis {
        // Deferred is the equivalent of Future in Java.
        val one: Deferred<Int> = async { doSomethingUsefulOne() }
        val two: Deferred<Int> = async { doSomethingUsefulTwo() }
        Log.info("Created async tasks.")
        Log.info("Waiting for the results ...")
        val answer = one.await().also { Log.info("One returned $it") } +
                two.await().also { Log.info("Two returned $it") }
        Log.info("The answer is $answer")
    }
    Log.info("Completed in $elapsedMillis ms")
}


suspend fun doSomethingUsefulOne(): Int {
    Log.info("Thinking ...")
    delay(ASYNC_TASK_DELAY) // Pretend we are doing something useful here.
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    Log.info("Computing ...")
    delay(ASYNC_TASK_DELAY) // Pretend we are doing something useful here, too.
    return 29
}
