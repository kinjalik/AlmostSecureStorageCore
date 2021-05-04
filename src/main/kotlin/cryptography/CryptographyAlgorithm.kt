package cryptography

interface CryptographyAlgorithm {
    fun encrypt(byteArray: ByteArray, key: ByteArray): ByteArray
    fun decrypt(byteArray: ByteArray, key: ByteArray): ByteArray
}