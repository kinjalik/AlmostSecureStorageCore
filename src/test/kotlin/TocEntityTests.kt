import storedComponents.TocEntity
import org.junit.jupiter.api.Test

class TocEntityTests {
    @Test
    fun `Serialization test`() {
        val originalTocEntity = TocEntity("University Account", 0, 100, 230239)
        val copyTocEntity = TocEntity.deserialize(originalTocEntity.serialize())
        println(originalTocEntity == copyTocEntity)
    }
}