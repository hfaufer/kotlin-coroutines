package de.hu.kotlin.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import java.time.Duration
import kotlin.time.Duration.Companion.seconds

fun main() {
    val job = GlobalScope.launch {
        println("Hello")
        delay(1.seconds)
        println(", world!")
    }
    sleep(Duration.ofSeconds(2))
}
