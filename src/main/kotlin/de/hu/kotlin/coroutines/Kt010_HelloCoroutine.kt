import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds

fun main() {
    println("Kt010_HelloCoroutine")
    show("Hello")

    // runBlocking runs a new coroutine in the current thread. That is, it blocks the current thread.
    // The rest of this function will not be executed until the coroutine and thereby runBlocking returns.
    runBlocking {
        show(",")
        delay(3.seconds)
        show(" world")
    }

    show("!")
}

fun show(message: String) {
    var threadName = Thread.currentThread().getName()
    System.out.printf("Thread [%20s]: %s%n", threadName, message)
}

