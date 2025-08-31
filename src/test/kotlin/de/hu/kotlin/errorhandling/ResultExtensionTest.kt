package de.hu.kotlin.errorhandling

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ResultExtensionTest {

    private val intException = Throwable("Int Exception")
    private val intFailure = Result.failure<Int>(intException)
    private val intSuccess42 = intSuccess(42)
    private val stringException = Throwable("String Exception")
    private val stringFailure = Result.failure<String>(stringException)
    private val stringSuccess42 = stringSuccess("42")

    @Nested
    inner class SingleResult {

        @Test
        fun `flatMap should return failure if receiver is failure`() {
            val receiver = stringFailure

            val result = receiver.flatMap { intSuccess(it.toInt()) }

            assertEquals(stringFailure, result)
        }

        @Test
        fun `flatMap should return failure if receiver is success but map result is failure`() {
            val receiver = stringSuccess42

            val result = receiver.flatMap { intFailure }

            assertEquals(intFailure, result)
        }

        @Test
        fun `flatMap should return mapped value if receiver and map result are success`() {
            val receiver = stringSuccess42

            val result = receiver.flatMap { intSuccess(it.toInt()) }

            assertEquals(intSuccess42, result)
        }
    }

    @Nested
    inner class ListOfResults {

        @Test
        fun `resultExceptions should return the exceptions of all failures`() {
            val receiver = listOf(intFailure, intSuccess42)

            val result = receiver.resultExceptions()

            assertEquals(listOf(intException), result)
        }

        @Test
        fun `resultValues should return the values of all successes`() {
            val receiver = listOf(intFailure, intSuccess42)

            val result = receiver.resultValues()

            assertEquals(listOf(42), result)
        }

        @Test
        fun `alsoResultExceptions should return the receiver`() {
            val receiver = listOf(intFailure, intSuccess42)

            val result = receiver.alsoResultValues { println(it) }

            assertSame(receiver, result)
        }

        @Test
        fun `alsoResultExceptions should call lambda with values of all successes`() {
            val receiver = listOf(intFailure, intSuccess42)

            val values = mutableListOf<Int>()
            val result = receiver.alsoResultValues { values.addAll(it) }

            assertSame(receiver, result)
            assertEquals(listOf(42), values)
        }

        @Test
        fun `filterResult should pass all failures and filter successes`() {
            val receiver = listOf(intSuccess(1), intFailure, intSuccess42)

            val result = receiver.filterResult { it > 12 }

            assertEquals(listOf(intFailure, intSuccess42), result)
        }

        @Test
        fun `flatMapResult should pass all failures`() {
            val receiver = listOf(stringFailure)

            val result = receiver.flatMapResult { listOf(intSuccess(it.toInt() + 1), intSuccess(it.toInt() + 2)) }

            assertEquals(listOf(stringFailure), result)
        }

        @Test
        fun `flatMapResult should flatMap all successes`() {
            val receiver = listOf(stringSuccess("1"), stringSuccess42)

            val result = receiver.flatMapResult { listOf(intSuccess(it.toInt() + 1), intSuccess(it.toInt() + 2)) }

            assertEquals(listOf(intSuccess(2), intSuccess(3), intSuccess(43), intSuccess(44)), result)
        }

        @Test
        fun `mapNotNullResult should pass all failures`() {
            val receiver = listOf(stringFailure)

            val result = receiver.mapNotNullResult { it.toInt() }

            assertEquals(listOf(stringFailure), result)
        }

        @Test
        fun `mapNotNullResult should map all successes`() {
            val receiver = listOf(stringSuccess42)

            val result = receiver.mapNotNullResult { it.toInt() }

            assertEquals(listOf(intSuccess42), result)
        }

        @Test
        fun `mapNotNullResult should drop all success mapped to null`() {
            val receiver = listOf(stringSuccess42)

            val result = receiver.mapNotNullResult { null }

            assertEquals(emptyList<Result<Int>>(), result)
        }

    }

    private fun intSuccess(x: Int) = Result.success(x)

    private fun stringSuccess(s: String) = Result.success(s)
}
