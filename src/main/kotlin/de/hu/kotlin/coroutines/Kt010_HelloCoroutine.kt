package de.hu.kotlin.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds

fun main() {
    println("Kt010_HelloCoroutine")
    show("Hello")

    // runBlocking() executes the given code block in a new coroutine that runs in the current thread.
    // That is, runBlocking() blocks the current thread until the coroutine return.
    // As you will see in the output, all code is executed by the same thread.
    runBlocking {
        show(",")
        delay(3.seconds) // delay() is a suspend function that can only be called in a coroutine.
        show(" world")
    }

    show("!")
}

fun show(message: String) {
    val threadName = Thread.currentThread().name
    System.out.printf("Thread [%s]: %s%n", threadName, message)
}
