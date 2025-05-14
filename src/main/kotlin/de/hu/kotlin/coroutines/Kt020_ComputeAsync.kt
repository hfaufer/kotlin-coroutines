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
    println("Starting computation ...")
    val elapsedMillis = measureTimeMillis {
        // Deferred is the equivalent of Future in Java.
        val one: Deferred<Int> = async { doSomethingUsefulOne() }
        val two: Deferred<Int> = async { doSomethingUsefulTwo() }
        println("Created async tasks.")
        println("Waiting for the results ...")
        val answer = one.await().also { println("One returned $it") } +
                two.await().also { println("Two returned $it") }
        println("The answer is $answer")
    }
    println("Completed in $elapsedMillis ms")
}


suspend fun doSomethingUsefulOne(): Int {
    println("Thinking ...")
    delay(ASYNC_TASK_DELAY) // Pretend we are doing something useful here.
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    println("Computing ...")
    delay(ASYNC_TASK_DELAY) // Pretend we are doing something useful here, too.
    return 29
}
