import storedComponents.Preamble
import cryptography.Algorithms
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PreambleTests {
    @Test
    fun `Serialization test`() {
        val originalPreambule = Preamble("Albert Akmukhametov", Algorithms.fromId(1), Integer.MAX_VALUE.hashCode(), 13337)
        val copyPreambule = Preamble.deserialize(originalPreambule.serialize())
        assertEquals(originalPreambule, copyPreambule)
    }
}