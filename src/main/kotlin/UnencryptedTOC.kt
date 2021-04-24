data class UnencryptedTOC(
    val encryptionAlgorithm: Byte,
    val encryptedPartChecksum: Int
) {
    fun serialize(): ByteArray {
        val res = mutableListOf<Byte>()

        val encAlgo = numericToByteList(encryptionAlgorithm) // ToDo: Add field extension mechanism
        res.addAll(encAlgo)
        val encCheckSum = numericToByteList(encryptedPartChecksum)
        res.addAll(encCheckSum)
        val size = (res.size + 1).toByte()
        res.add(0, size)

        return res.toByteArray()
    }

    private fun numericToByteList(value: Number): List<Byte> {
        val res = mutableListOf<Byte>()
        var cur = value

        val bytes = when(value) {
            is Int -> Int.SIZE_BYTES
            is Long -> Long.SIZE_BYTES
            is Byte -> Byte.SIZE_BYTES
            else -> throw SerializationException("Unknown numeric type for serialization")
        }

        for (i in 1..bytes) {
            res.add(cur.toByte())
            when (cur) {
                is Int -> cur = cur.shr(8)
                is Long -> cur = cur.shr(8)
                is Byte -> break
            }
        }
        return res.reversed().toList()
    }
}
