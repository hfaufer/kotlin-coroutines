package de.hu.kotlin.concurrency

import de.hu.kotlin.coroutines.Log
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.milliseconds

fun main() {
//    val demo = CoroutineScopeDemo()
//    val demo = CoroutineScopeDemo(Dispatchers.IO)
    val demo = CoroutineScopeDemo(Dispatchers.VT)
    demo.start()
    Thread.sleep(2000)
    demo.stop()
}

class CoroutineScopeDemo(dispatcher: CoroutineDispatcher = Dispatchers.Default) {
    private val scope = CoroutineScope(dispatcher + SupervisorJob())

    fun start() {
        Log.info("Starting!")
        scope.launch {
            while (true) {
                delay(500.milliseconds)
                Log.info("Component working!")
            }
        }
        scope.launch {
            Log.info("Doing a one-off task...")
            delay(500.milliseconds)
            Log.info("Task done!")
        }
    }

    fun stop() {
        Log.info("Stopping!")
        scope.cancel()
    }
}
