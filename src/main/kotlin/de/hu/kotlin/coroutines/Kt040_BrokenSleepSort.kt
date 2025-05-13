import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds

fun main() {
    println("Kt040_BrokenSleepSort")
    sleepSort(listOf(3, 1, 4, 2))
}

private fun sleepSort(elements: List<Int>): Unit =
    // By using runBlocking, the elements are actually revealed sequentially and not printed in ascending order.
    elements.forEach {
        runBlocking { reveal(it) }
    }

private suspend fun reveal(number: Int): Unit {
    delay(number.seconds)
    println("$number revealed by thread ${Thread.currentThread().name}")
}
