package de.hu.kotlin.concurrency

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

internal val Dispatchers.VT: CoroutineDispatcher
    get() {
        // In a real application, you would use Dispatchers.VT from kotlinx.coroutines.
        return Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()
    }
