package mockito

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.InOrder
import org.mockito.kotlin.*
import java.util.*

class CoroutinesTest {

    @Test
    fun stubbingSuspendingInterface() {
        /* Given */
        val m = mock<CoInterface> {
            onBlocking { suspending() } doReturn 42
        }

        /* When */
        val result = runBlocking { m.suspending() }

        /* Then */
        assertEquals(42, result)
    }

    @Test
    fun stubbingSuspendingImplementation() {
        /* Given */
        val m: CoImplementation = mock { }
        m.stub {
            onBlocking { suspending() } doReturn 137
        }

        /* When */
        val result = runBlocking { m.suspending() }

        /* Then */
        assertEquals(137, result)
    }

    @Test
    fun stubbingSuspendingInterface_usingSuspendingFunction() {
        /* Given */
        val m = mock<CoInterface> {
            onBlocking { suspending() } doReturn runBlocking { ClassSwitchingCoContext().result(42) }
        }

        /* When */
        val result = runBlocking { m.suspending() }

        /* Then */
        assertEquals(42, result)
    }

    @Test
    fun stubbingSuspendingImplementation_usingSuspendingFunction() {
        /* Given */
        val m: CoImplementation = mock {}
        m.stub {
            onBlocking { suspending() } doReturn runBlocking { ClassSwitchingCoContext().result(42) }
        }

        /* When */
        val result = runBlocking { m.suspending() }

        /* Then */
        assertEquals(42, result)
    }

    @Test
    fun stubbingSuspendingInterface_runBlocking() = runBlocking {
        /* Given */
        val m = mock<CoInterface> {
            onBlocking { suspending() } doReturn 42
        }

        /* When */
        val result = m.suspending()

        /* Then */
        assertEquals(42, result)
    }

    @Test
    fun stubbingSuspendingInterface_wheneverBlocking() {
        /* Given */
        val m: CoInterface = mock()
        wheneverBlocking { m.suspending() }
            .doReturn(42)

        /* When */
        val result = runBlocking { m.suspending() }

        /* Then */
        assertEquals(42, result)
    }

    @Test
    fun stubbingSuspendingInterface_doReturn() {
        /* Given */
        val m = spy(ClassSwitchingCoContext())
        doReturn(10)
            .wheneverBlocking(m) {
                delaying()
            }

        /* When */
        val result = runBlocking { m.delaying() }

        /* Then */
        assertEquals(10, result)
    }

    @Test
    fun stubbingNonSuspending() {
        /* Given */
        val m = mock<CoInterface> {
            onBlocking { nonSuspending() } doReturn 42
        }

        /* When */
        val result = m.nonSuspending()

        /* Then */
        assertEquals(42, result)
    }

    @Test
    fun stubbingNonSuspending_runBlocking() = runBlocking {
        /* Given */
        val m = mock<CoInterface> {
            onBlocking { nonSuspending() } doReturn 42
        }

        /* When */
        val result = m.nonSuspending()

        /* Then */
        assertEquals(42, result)
    }

    @Test
    fun delayingResult() {
        /* Given */
        val m = ClassSwitchingCoContext()

        /* When */
        val result = runBlocking { m.delaying() }

        /* Then */
        assertEquals(42, result)
    }

    @Test
    fun delayingResult_runBlocking() = runBlocking {
        /* Given */
        val m = ClassSwitchingCoContext()

        /* When */
        val result = m.delaying()

        /* Then */
        assertEquals(42, result)
    }

    @Test
    fun verifySuspendFunctionCalled() {
        /* Given */
        val m = mock<CoInterface>()

        /* When */
        runBlocking { m.suspending() }

        /* Then */
        runBlocking { verify(m).suspending() }
    }

    @Test
    fun verifySuspendFunctionCalled_runBlocking() = runBlocking<Unit> {
        val m = mock<CoInterface>()

        m.suspending()

        verify(m).suspending()
    }

    @Test
    fun verifySuspendFunctionCalled_verifyBlocking() {
        val m = mock<CoInterface>()

        runBlocking { m.suspending() }

        verifyBlocking(m) { suspending() }
    }

    @Test
    fun verifyAtLeastOnceSuspendFunctionCalled_verifyBlocking() {
        val m = mock<CoInterface>()

        runBlocking { m.suspending() }
        runBlocking { m.suspending() }

        verifyBlocking(m, atLeastOnce()) { suspending() }
    }

    @Test
    fun verifySuspendMethod() = runBlocking {
        val testSubject: CoInterface = mock()

        testSubject.suspending()

        inOrder(testSubject) {
            verify(testSubject).suspending()
        }
    }

    @Test
    fun answerWithSuspendFunction() = runBlocking {
        val fixture: CoInterface = mock()

        whenever(fixture.suspendingWithArg(any())).doSuspendableAnswer {
            withContext(Dispatchers.Default) { it.getArgument<Int>(0) }
        }

        assertEquals(5, fixture.suspendingWithArg(5))
    }

    @Test
    fun inplaceAnswerWithSuspendFunction() = runBlocking {
        val fixture: CoInterface = mock {
            onBlocking { suspendingWithArg(any()) } doSuspendableAnswer {
                withContext(Dispatchers.Default) { it.getArgument<Int>(0) }
            }
        }

        assertEquals(5, fixture.suspendingWithArg(5))
    }

    @Test
    fun callFromSuspendFunction() = runBlocking {
        val fixture: CoInterface = mock()

        whenever(fixture.suspendingWithArg(any())).doSuspendableAnswer {
            withContext(Dispatchers.Default) { it.getArgument<Int>(0) }
        }

        val result = async {
            val answer = fixture.suspendingWithArg(5)

            Result.success(answer)
        }

        assertEquals(5, result.await().getOrThrow())
    }

    @Test
    fun callFromActor() = runBlocking {
        val fixture: CoInterface = mock()

        whenever(fixture.suspendingWithArg(any())).doSuspendableAnswer {
            withContext(Dispatchers.Default) { it.getArgument<Int>(0) }
        }

        val actor = actor<Optional<Int>> {
            for (element in channel) {
                fixture.suspendingWithArg(element.get())
            }
        }

        actor.send(Optional.of(10))
        actor.close()

        verify(fixture).suspendingWithArg(10)

        Unit
    }

    @Test
    fun answerWithSuspendFunctionWithoutArgs() = runBlocking {
        val fixture: CoInterface = mock()

        whenever(fixture.suspending()).doSuspendableAnswer {
            withContext(Dispatchers.Default) { 42 }
        }

        assertEquals(42, fixture.suspending())
    }

    @Test
    fun answerWithSuspendFunctionWithDestructuredArgs() = runBlocking {
        val fixture: CoInterface = mock()

        whenever(fixture.suspendingWithArg(any())).doSuspendableAnswer { (i: Int) ->
            withContext(Dispatchers.Default) { i }
        }

        assertEquals(5, fixture.suspendingWithArg(5))
    }

    @Test
    fun willAnswerWithControlledSuspend() = runBlocking {
        val fixture: CoInterface = mock()

        val job = Job()

        whenever(fixture.suspending()).doSuspendableAnswer {
            job.join()
            5
        }

        val asyncTask = async {
            fixture.suspending()
        }

        job.complete()

        withTimeout(100) {
            assertEquals(5, asyncTask.await())
        }
    }

    @Test
    fun inOrderRemainsCompatible() {
        /* Given */
        val fixture: CoInterface = mock()

        /* When */
        val inOrder = inOrder(fixture)

        /* Then */
        assertInstanceOf(InOrder::class.java, inOrder)
    }

    @Test
    fun inOrderSuspendingCalls() {
        /* Given */
        val fixtureOne: CoInterface = mock()
        val fixtureTwo: CoInterface = mock()

        /* When */
        runBlocking {
            fixtureOne.suspending()
            fixtureTwo.suspending()
        }

        /* Then */
        val inOrder = inOrder(fixtureOne, fixtureTwo)
        inOrder.verifyBlocking(fixtureOne) { suspending() }
        inOrder.verifyBlocking(fixtureTwo) { suspending() }
    }

    @Test
    fun inOrderSuspendingCallsFailure() {
        /* Given */
        val fixtureOne: CoInterface = mock()
        val fixtureTwo: CoInterface = mock()

        /* When */
        runBlocking {
            fixtureOne.suspending()
            fixtureTwo.suspending()
        }

        /* Then */
        val inOrder = inOrder(fixtureOne, fixtureTwo)
        inOrder.verifyBlocking(fixtureTwo) { suspending() }
        assertThrows(AssertionError::class.java) {
            inOrder.verifyBlocking(fixtureOne) { suspending() }
        }
    }

    @Test
    fun inOrderBlockSuspendingCalls() {
        /* Given */
        val fixtureOne: CoInterface = mock()
        val fixtureTwo: CoInterface = mock()

        /* When */
        runBlocking {
            fixtureOne.suspending()
            fixtureTwo.suspending()
        }

        /* Then */
        inOrder(fixtureOne, fixtureTwo) {
            verifyBlocking(fixtureOne) { suspending() }
            verifyBlocking(fixtureTwo) { suspending() }
        }
    }

    @Test
    fun inOrderBlockSuspendingCallsFailure() {
        /* Given */
        val fixtureOne: CoInterface = mock()
        val fixtureTwo: CoInterface = mock()

        /* When */
        runBlocking {
            fixtureOne.suspending()
            fixtureTwo.suspending()
        }

        /* Then */
        inOrder(fixtureOne, fixtureTwo) {
            verifyBlocking(fixtureTwo) { suspending() }
            assertThrows(AssertionError::class.java) {
                verifyBlocking(fixtureOne) { suspending() }
            }
        }
    }

    @Test
    fun inOrderOnObjectSuspendingCalls() {
        /* Given */
        val fixture: CoInterface = mock()

        /* When */
        runBlocking {
            fixture.suspendingWithArg(1)
            fixture.suspendingWithArg(2)
        }

        /* Then */
        fixture.inOrder {
            verifyBlocking { suspendingWithArg(1) }
            verifyBlocking { suspendingWithArg(2) }
        }
    }

    @Test
    fun inOrderOnObjectSuspendingCallsFailure() {
        /* Given */
        val fixture: CoInterface = mock()

        /* When */
        runBlocking {
            fixture.suspendingWithArg(1)
            fixture.suspendingWithArg(2)
        }

        /* Then */
        fixture.inOrder {
            verifyBlocking { suspendingWithArg(2) }
            assertThrows(AssertionError::class.java) {
                verifyBlocking { suspendingWithArg(1) }
            }
        }
    }
}

interface CoInterface {

    suspend fun suspending(): Int
    suspend fun suspendingWithArg(arg: Int): Int
    fun nonSuspending(): Int
}

open class CoImplementation {

    suspend fun suspending(): Int {
        delay(0)
        return -1
    }
}

open class ClassSwitchingCoContext {

    suspend fun result(r: Int) = withContext(Dispatchers.Default) { r }

    open suspend fun delaying() = withContext(Dispatchers.Default) {
        delay(100)
        42
    }
}
