import cryptography.Algorithms
import operationalComponents.StorageData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import storedComponents.DataEntity
import storedComponents.Preamble
import storedComponents.TocEntity
import kotlin.random.Random

class SerializationTests {
    val random = Random(264726827)
    val testAuthorName = "Albert Akmukhametov"

    // Storage Data
    @Test
    fun `Storage Data Serialization`() {
        val storage = StorageData("Albert Akmukhametov", Algorithms.AES128CBC)
        val password = random.nextBytes(20)

        val encBytes = storage.getResult(password)
        val deEncStorageData = StorageData.read(password, encBytes)

        assertEquals(storage.author, deEncStorageData.author)
    }

    // Preamble
    val testAlgorithm = Algorithms.fromId(1)
    val testPreambleChecksum = Int.MAX_VALUE.hashCode()
    val testPreambleSize = Int.MAX_VALUE / 2
    @Test
    fun `Preamble Serialization Test`() {
        val originalPreambule = Preamble(testAuthorName, testAlgorithm, testPreambleChecksum, testPreambleSize)
        val serialized = originalPreambule.serialize()
        val copyPreambule = Preamble.deserialize(serialized)
        assertEquals(originalPreambule, copyPreambule)
    }

    // Data Entity
    @Test
    fun `DataEntity Serialization test`() {
        val test = HashMap<String, String>()
        test["afwaf"] = "lkjawlfka"
        test["biozjx"] = "oijawioc"
        val originalDataEntity = DataEntity("TESTENTITY EPTA", test)

        val serialized = originalDataEntity.serialize()
        val copyDataEntity = DataEntity.deserialize(serialized)

        assertEquals(originalDataEntity, copyDataEntity)
    }

    // Table of Content Entity (TocEntity)
    val testEntityName = "University Account"
    val testContentOffset = Int.MAX_VALUE - 1
    val testContentSize = Int.MAX_VALUE - 1
    val testContentChecksum = Int.MIN_VALUE.hashCode()
    @Test
    fun `TocEntity Serialization Test`() {
        val originalTocEntity = TocEntity(testEntityName, testContentOffset, testContentSize, testContentChecksum)
        val serialized = originalTocEntity.serialize()
        val copyTocEntity = TocEntity.deserialize(serialized)

        assertEquals(originalTocEntity, copyTocEntity)
    }
}
