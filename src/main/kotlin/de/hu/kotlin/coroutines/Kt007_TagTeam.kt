package de.hu.kotlin.coroutines

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

const val NUMBER_OF_UNINTERRUPTED_ACTIONS = 5

fun main() = runBlocking {
    println("Kt007_TagTeam")

    val seldge = launch {
        repeat(NUMBER_OF_UNINTERRUPTED_ACTIONS) {
            println("Slegde: Punch!")
        }
        println("Slegde: Suplex!")
        tagOut()
        println("Slegde: Figure-four Leglock!")
        tagOut()
        println("Slegde: Pinning 1-2-3!")
    }
    val hammer = launch {
        repeat(NUMBER_OF_UNINTERRUPTED_ACTIONS) {
            println("Hammer: Pinch!")
        }
        println("Hammer: Clothesline!")
        tagOut()
        println("Hammer: Piledriver!")
        tagOut()
    }

    seldge.join()
    hammer.join()
    println("Done!")
}

private suspend fun tagOut() {
    println("Tag out!")
    yield()
}
