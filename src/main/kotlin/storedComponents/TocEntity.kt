package storedComponents

data class TocEntity(
    var name: String,
    var contentOffset: Int,
    var contentSize: Int,
    var contentChecksum: Int
) {
    val size: Int
        get() {
            var res = 0
            // Size field
            res += Int.SIZE_BYTES
            // name size
            res += Int.SIZE_BYTES
            // name
            res += nameSize
            // offset
            res += Int.SIZE_BYTES
            // size
            res += Int.SIZE_BYTES
            // checksum
            res += Int.SIZE_BYTES

            return res
        }

    val nameSize: Int
        get() = name.length

    fun serialize(): ByteArray {
        val res = mutableListOf<Byte>()
        res += Utils.serialize(size)
        res += Utils.serialize(nameSize)
        res += Utils.serialize(name)
        res += Utils.serialize(contentOffset)
        res += Utils.serialize(contentSize)
        res += Utils.serialize(contentChecksum)
        return res.toByteArray()
    }

    companion object {
        fun deserialize(v: ByteArray): TocEntity {
            val lst = v.asList()
            var ptr = 0

            val size: Int = Utils.deserializeInt(lst.subList(ptr, Int.SIZE_BYTES))
            ptr += Int.SIZE_BYTES

            val nameSize: Int = Utils.deserializeInt(lst.subList(ptr, ptr + Int.SIZE_BYTES))
            ptr += Int.SIZE_BYTES

            val name: String = Utils.deserializeString(lst.subList(ptr, ptr + nameSize), nameSize)
            ptr += nameSize

            val contentOffset: Int = Utils.deserializeInt(lst.subList(ptr, ptr + Int.SIZE_BYTES))
            ptr += Int.SIZE_BYTES

            val contentSize: Int = Utils.deserializeInt(lst.subList(ptr, ptr + Int.SIZE_BYTES))
            ptr += Int.SIZE_BYTES

            val contentChecksum: Int = Utils.deserializeInt(lst.subList(ptr, ptr + Int.SIZE_BYTES))
            ptr += Int.SIZE_BYTES

            return TocEntity(name, contentOffset, contentSize, contentChecksum)
        }
    }
}
