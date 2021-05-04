package storedComponents

import Utils
import cryptography.Algorithms

data class Preamble(
    var author: String,
    var encryptionAlgo: Algorithms,
    var tocChecksum: Int,
    var tocSize: Int
) {
    val authorSize: Int
        get() = author.length

    val size: Int
        get() {
            var res: Int = 0
            // Size of preamble
            res += Int.SIZE_BYTES
            // Author length
            res += Int.SIZE_BYTES
            res += authorSize
            // Encryption algo
            res += Byte.SIZE_BYTES
            // Checksum size
            res += Int.SIZE_BYTES
            res += Int.SIZE_BYTES
            return res
        }

    fun serialize(): ByteArray {
        val res = mutableListOf<Byte>()

//        println("Size of preamble: $sizeOfPreamble")
//        println(Utils.serialize(sizeOfPreamble))
        res.addAll(Utils.serialize(size))

//        println("Author size: $authorSize")
//        println(Utils.serialize(authorSize))
        res.addAll(Utils.serialize(authorSize))

//        println("Author: $author")
//        println(Utils.serialize(author))
        res.addAll(Utils.serialize(author))

//        println("Enc algo: $encryptionAlgo")
//        println(Utils.serialize(encryptionAlgo))
        res.addAll(Utils.serialize(encryptionAlgo.id.toByte()))

//        println("checksum: $tocChecksum")
//        println(Utils.serialize(tocChecksum))
        res.addAll(Utils.serialize(tocChecksum))

//        println("size: $tocSize")
//        println(Utils.serialize(tocSize))
        res.addAll(Utils.serialize(tocSize))
//        print("\n")

        return res.toByteArray()
    }

    companion object {
        fun deserialize(v: ByteArray): Preamble {
            val lst = v.asList()
            var ptr = 0

            val sizeOfPreamble: Int = Utils.deserializeInt(lst)
            ptr += Int.SIZE_BYTES
//            println("Size of preamble: $sizeOfPreamble")

            val authorLength: Int = Utils.deserializeInt(lst.subList(ptr, ptr + Int.SIZE_BYTES))
            ptr += Int.SIZE_BYTES
//            println("Author size: $authorLength")

            val author: String = Utils.deserializeString(lst.subList(ptr, ptr + authorLength), authorLength)
            ptr += authorLength
//            println("Author: $author")

            val encryptionAlgo: Byte = Utils.deserializeByte(lst.subList(ptr, ptr + Byte.SIZE_BYTES), 0)
            ptr += Byte.SIZE_BYTES
//            println("Enc algo: $encryptionAlgo")

            val tocChecksum: Int = Utils.deserializeInt(lst.subList(ptr, ptr + Int.SIZE_BYTES))
            ptr += Int.SIZE_BYTES
//            println("checksum: $tocChecksum")

            val tocSize: Int = Utils.deserializeInt(lst.subList(ptr, ptr + Int.SIZE_BYTES))
            ptr += Int.SIZE_BYTES
//            println("size: $tocSize")

            return Preamble(author, Algorithms.fromId(encryptionAlgo), tocChecksum, tocSize)
        }
    }
}