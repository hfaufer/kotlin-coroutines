package de.hu.kotlin.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// runBlocking is a coroutine builder that bridges the non-coroutine world of a regular
// fun main() and the code with coroutines inside the block { ... } of runBlocking.
// As the name of runBlocking implies, the thread that runs it (in this case — the main
// thread) gets blocked for the duration of the call, until all the coroutines inside
// runBlocking { ... } finished their execution.
fun main() = runBlocking() {
    // launch is another coroutine builder. It launches a new coroutine that executes the
    // code in the body concurrently to the main thread.
    // That's why Hello will be printed first.
    launch {
        printWorld()
    }
    println("Hello,")
}

// Suspending functions can be used inside coroutines just like regular functions, but their
// additional feature is that they can, in turn, use other suspending functions (like delay in
// this example) to suspend execution of a coroutine.
private suspend fun printWorld() {
    // delay is a special suspending function. It suspends the coroutine for a specific time.
    // Suspending a coroutine does not block the carrier thread, but allows other coroutines
    // to run and use the carrier thread for their code.
    delay(1234L)
    println("World!")
}
