package storedComponents

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class DataEntity(
    var name: String,
    val properties: Map<String, String>
) {
    val nameSize: Int
        get() = name.length

    val propertiesSize: Int
        get() = Json.encodeToString(properties).length

    fun serialize(): ByteArray {
        val res = mutableListOf<Byte>()
        res += Utils.serialize(nameSize)
        res += Utils.serialize(name)
        res += Utils.serialize(propertiesSize)
        res += Utils.serialize(Json.encodeToString(properties))
        return res.toByteArray()
    }

    companion object {
        fun deserialize(v: ByteArray): DataEntity {
            val lst = v.asList()
            var ptr = 0

            val nameSize: Int = Utils.deserializeInt(lst.subList(ptr, ptr + Int.SIZE_BYTES))
            ptr += Int.SIZE_BYTES

            val name: String = Utils.deserializeString(lst.subList(ptr, ptr + nameSize), nameSize)
            ptr += nameSize

            val propertiesSize: Int = Utils.deserializeInt(lst.subList(ptr, ptr + Int.SIZE_BYTES))
            ptr += Int.SIZE_BYTES

            val propertiesStr: String = Utils.deserializeString(lst.subList(ptr, ptr + propertiesSize), propertiesSize)
            val properties = Json.decodeFromString<HashMap<String, String>>(propertiesStr)
            ptr += propertiesSize

            return DataEntity(name, properties)

        }
    }
}