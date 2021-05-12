import exceptions.SerializationException

fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

fun println(v: List<Byte>) {
    println(v.toByteArray().toHexString())
}

fun println(v: Byte) {
    println(listOf(v).toByteArray().toHexString())
}

object Utils {
    fun serialize(v: Long): List<Byte> {
        var cur = v
        val res = mutableListOf<Byte>()

        repeat(Long.SIZE_BYTES) {
            res.add(cur.toByte())
            cur = cur shr Byte.SIZE_BITS
        }
        return res.reversed().toList()
    }

    fun serialize(v: Int): List<Byte> {
        var cur = v
        val res = mutableListOf<Byte>()

        repeat(Int.SIZE_BYTES) {
            res.add(cur.toByte())
            cur = cur shr Byte.SIZE_BITS
        }
        return res.reversed().toList()
    }

    fun serialize(v: String): List<Byte> {
        val res = mutableListOf<Byte>()
        v.forEach {
            res.add(it.toByte())
        }
        return res.toList()
    }

    fun serialize(v: Byte): List<Byte> {
        return listOf(v)
    }

    fun <T> serialize(v: T): List<Byte> {
        throw SerializationException("Unknown serialization way for ${if (v != null) v!!::class.simpleName else "unknown"}")
    }

    fun deserializeLong(stream: List<Byte>): Long {
        var res = 0L
        repeat(Long.SIZE_BYTES) {
            res = res shl Byte.SIZE_BITS
            res = res or (stream[it].toLong() and 0xffL)
        }
        return res
    }

    fun deserializeInt(stream: List<Byte>): Int {
        var res = 0
        repeat(Int.SIZE_BYTES) {
            res = res shl Byte.SIZE_BITS
            res = res or (stream[it].toInt() and 0xff)
        }
        return res
    }

    fun deserializeString(stream: List<Byte>, length: Int): String {
        var res = ""

        repeat(length) {
            res += stream[it].toChar()
        }

        return res
    }

    fun deserializeByte(lst: List<Byte>, ptr: Int): Byte {
        return lst[ptr]
    }
}
