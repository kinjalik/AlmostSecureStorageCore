package cryptography

enum class Algorithms(val id: Int) {
    AES128CBC(1);
    companion object {
        fun fromId(id: Byte): Algorithms {
            return when (id) {
                1.toByte() -> Algorithms.AES128CBC
                else -> Algorithms.AES128CBC
            }
        }
    }
}

fun Algorithms.getAlgorithm(): CryptographyAlgorithm {
    return when (id) {
        1 -> AesAlgorithm()
        else -> AesAlgorithm() // Default algo
    }
}

