package de.hu.kotlin.coroutines

import java.nio.file.Files
import java.util.logging.Logger
import kotlin.io.path.Path

internal val logger: Logger = Logger.getLogger("de.hu.kotlin.coroutines")

internal object Log {
    init {
        val propertyKey = "java.util.logging.config.file"
        val configFile: String? = System.getProperty(propertyKey)
        if (configFile == null) {
            println("WARNING: Property $propertyKey not set!")
        } else if (!Files.exists(Path(configFile))) {
            println("ERROR: Configuration file '$propertyKey' not found!")
        }
    }

    fun info(message: String): Log {
        logger.info("[${threadName()}]: $message")
        return this
    }

    private fun threadName(): String? {
        val currentThread = Thread.currentThread()
        return currentThread.name.ifBlank { currentThread.threadId().toString() }
    }
}
