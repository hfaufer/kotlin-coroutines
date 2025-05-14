package de.hu.kotlin.coroutines

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Demonstrates coroutines without using the kotlinx.coroutines library.
 * From https://www.youtube.com/watch?v=kIzjzjJGk0Y[The Essence of Coroutines, Dave Leeds]
 */
fun main() {
    println("Kt001_CoroutinesWithoutKotlinX")
    milkCows()
}

private fun milkCows() {
    var cow = 0
    println("Milking cow ${++cow}"); feedChickens.resume()
    println("Milking cow ${++cow}"); feedChickens.resume()
    println("Milking cow ${++cow}"); feedChickens.resume()
    println("Milking cow ${++cow}"); feedChickens.resume()
}

private val feedChickens = createCoroutine {
    var chicken = 0
    println("Feeding chicken ${++chicken}"); yield()
    println("Feeding chicken ${++chicken}"); yield()
    println("Feeding chicken ${++chicken}"); yield()
    println("Feeding chicken ${++chicken}"); complete()
}

private fun <T> createCoroutine(block: suspend () -> T): Continuation<Unit> =
    block.createCoroutineUnintercepted(Continuation(EmptyCoroutineContext) {})

private fun Continuation<Unit>.resume(): Unit = resume(Unit)

private suspend fun yield(): Unit = suspendCoroutine { COROUTINE_SUSPENDED }

private suspend fun complete(): Unit = suspendCoroutine { it.resume(Unit) }
