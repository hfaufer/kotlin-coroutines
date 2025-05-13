import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds

fun main() {
    println("Kt050_SleepSort")
    sleepSort(listOf(3, 1, 4, 2))
}

private fun sleepSort(elements: List<Int>): Unit =
    runBlocking {
        val jobs = elements.map {
            launch { reveal(it) }
        }
        jobs.forEach { it.join() }
    }

private suspend fun reveal(number: Int): Unit {
    delay(number.seconds)
    println("$number revealed by thread ${Thread.currentThread().name}")
}
