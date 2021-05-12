package cryptography

interface CryptographyAlgorithm {
    fun encrypt(byteArray: ByteArray, password: ByteArray): ByteArray
    fun decrypt(byteArray: ByteArray, password: ByteArray): ByteArray
}