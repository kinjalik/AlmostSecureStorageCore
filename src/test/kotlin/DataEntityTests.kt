import storedComponents.DataEntity
import cryptography.AesAlgorithm
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DataEntityTests {
    @Test
    fun `Serialization test`() {
        val test = HashMap<String, String>()
        test["afwaf"] = "lkjawlfka"
        test["biozjx"] = "oijawioc"
        val originalDataEntity = DataEntity("TESTENTITY EPTA", test)
        val copyDataEntity = DataEntity.deserialize(originalDataEntity.serialize())
        assertEquals(originalDataEntity, copyDataEntity)
    }

    @Test
    fun `AES128CBC Encryption test`() {
        val algo = AesAlgorithm()
        val testKey = "TEST KEY FOR EVERYTHING LOL".toByteArray()

        val test = HashMap<String, String>()
        test["afwaf"] = "lkjawlfka"
        test["biozjx"] = "oijawioc"
        val originalDataEntity = DataEntity("TESTENTITY EPTA", test)
        val originalBytes = originalDataEntity.serialize()
        val enc = algo.encrypt(originalBytes, testKey)
        val copyBytes = algo.decrypt(enc, testKey)
        val copyEntity = DataEntity.deserialize(copyBytes)
        assertEquals(originalDataEntity, copyEntity)
    }
}