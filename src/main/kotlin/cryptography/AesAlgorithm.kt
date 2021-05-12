package cryptography

import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class AesAlgorithm : CryptographyAlgorithm {

    private fun passwordToKey(password: ByteArray): ByteArray {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(password)
    }

    override fun encrypt(byteArray: ByteArray, password: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES")
        val cipherKey = SecretKeySpec(passwordToKey(password), "AES")
        cipher.init(Cipher.ENCRYPT_MODE, cipherKey)
        return cipher.doFinal(byteArray)
    }

    override fun decrypt(byteArray: ByteArray, password: ByteArray): ByteArray {
        val key = SecretKeySpec(passwordToKey(password), "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, key)

        val decrypted = cipher.doFinal(byteArray)
        return decrypted
    }
}
