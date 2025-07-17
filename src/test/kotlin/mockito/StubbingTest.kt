package mockito

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class StubbingTest {

    private lateinit var interfaceMock: AnInterface

    @BeforeEach
    fun setUp() {
        interfaceMock = mock {
            on { method() }.doReturn(-21)
            on { function(1) }.doReturn(-1)
            on { function(2) }.doReturn(-2)
        }
    }

    @Test
    fun `mock should return stubbed value`() {
        assertEquals(-21, interfaceMock.method())
        assertEquals(-1, interfaceMock.function(1))
        assertEquals(-2, interfaceMock.function(2))
    }

    @Test
    fun `mock should return stubbed override value`() {
        whenever(interfaceMock.method()).thenReturn(1)
        assertEquals(1, interfaceMock.method())

        whenever(interfaceMock.function(1)).thenReturn(10)
        assertEquals(10, interfaceMock.function(1))

        whenever(interfaceMock.function(2)).thenReturn(20)
        assertEquals(20, interfaceMock.function(2))
    }

    @Test
    fun `whenever should stub the return value of a method`() {
        val mock = mock<AnInterface>()
        whenever(mock.method()).doReturn(42)

        assert(mock.method() == 42)
    }

    @Test
    fun `on should stub the return value of a method`() {
        val mock = mock<AnInterface> {
            on { method() }.doReturn(43)
        }

        assert(mock.method() == 43)
    }
}

interface AnInterface {

    fun method(): Int
    fun function(arg: Int): Int
}
