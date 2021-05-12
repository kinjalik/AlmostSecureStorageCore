import cryptography.AesAlgorithm
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import storedComponents.DataEntity
import kotlin.random.Random

class EncryptionTests {
    val random = Random(System.currentTimeMillis())
    val testKey = "TEST KEY FOR EVERYTHING I DON'T KNOW WHY".toByteArray()
    @Test
    fun `AES128CBC Encryption test`() {
        val cipher = AesAlgorithm()
        val sourceData = random.nextBytes(10000)

        val encryptedData = cipher.encrypt(sourceData, testKey)
        val decryptedData = cipher.decrypt(encryptedData, testKey)

        assertEquals(sourceData.toList(), decryptedData.toList())
    }
}