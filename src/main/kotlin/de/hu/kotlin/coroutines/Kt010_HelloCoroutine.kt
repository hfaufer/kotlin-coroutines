package de.hu.kotlin.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.util.logging.Logger
import kotlin.io.path.Path
import kotlin.time.Duration.Companion.seconds

val logger: Logger = Logger.getLogger("de.hu.kotlin.coroutines.Kt010_HelloCoroutine")

fun main() {
    println("Kt010_HelloCoroutine")
    val configFile = System.getProperty("java.util.logging.config.file")
    println("$configFile exists? " + Files.exists(Path(configFile)))
    log("Hello")

    // runBlocking() executes the given code block in a new coroutine that runs in the current thread.
    // That is, runBlocking() blocks the current thread until the coroutine return.
    // As you will see in the output, all code is executed by the same thread.
    runBlocking {
        log(",")
        delay(3.seconds) // delay() is a suspend function that can only be called in a coroutine.
        log(" world")
    }

    log("!")
}

fun log(message: String) {
    val threadName = Thread.currentThread().name
    logger.info("[$threadName]: $message")
}
