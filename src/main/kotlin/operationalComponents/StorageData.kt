package operationalComponents

import cryptography.Algorithms
import cryptography.getAlgorithm
import exceptions.AlreadyExistsException
import exceptions.EntityNotFoundException
import storedComponents.DataEntity
import storedComponents.Preamble
import storedComponents.TocEntity
import toHexString

class StorageData(
    val author: String,
    private val encAlgorithmId: Algorithms = Algorithms.AES128CBC
) {
    private val encAlgorithm = encAlgorithmId.getAlgorithm()

    private var internalEntityList: MutableList<TocEntity> = mutableListOf()
    private var dataMemory: MutableList<Byte> = mutableListOf()

    val entities
        get() = internalEntityList.toList()

    fun addEntity(password: ByteArray, name: String, props: Map<String, String>): TocEntity {
        if (findByName(name) != -1)
            throw AlreadyExistsException("Entity with name \"$name\" already exists.")

        val contentOffset = dataMemory.size

        val dataEntity = DataEntity(name, props).serialize()
        val hiddenDataEntity: ByteArray = encAlgorithm.encrypt(dataEntity, password)

        val dataList = hiddenDataEntity.asList()
        dataMemory.addAll(dataList)

        val contentSize = dataList.size
        val contentChecksum = dataEntity.hashCode()

        val tocEntity = TocEntity(name, contentOffset, contentSize, contentChecksum)
        internalEntityList.add(tocEntity)
        return tocEntity
    }

    fun getEntity(password: ByteArray, entity: TocEntity): DataEntity =
        getEntity(password, entity.name)

    fun getEntity(password: ByteArray, name: String): DataEntity {
        val entityIndex = findByName(name)
        if (entityIndex == -1)
            throw EntityNotFoundException("No entity $name found")
        val entity = internalEntityList[entityIndex]

        val offset = entity.contentOffset
        val size = entity.contentSize
        val encrypted: ByteArray = dataMemory.subList(offset, size + offset).toByteArray()
        val decrypted = encAlgorithm.decrypt(encrypted, password)
        return DataEntity.deserialize(decrypted)
    }

    fun updateEntity(password: ByteArray, entity: TocEntity, newName: String, newProps: Map<String, String>): TocEntity =
        updateEntity(password, entity.name, newName, newProps)

    fun updateEntity(password: ByteArray, oldName: String, newName: String, newProps: Map<String, String>): TocEntity {
        val entityIndex = findByName(oldName)
        if (entityIndex == -1)
            throw EntityNotFoundException("Entity $oldName not found.")
        val entity = internalEntityList[entityIndex]
        val offset = entity.contentOffset
        val oldSize = entity.contentSize

        val dataEntity = DataEntity(newName, newProps).serialize()
        val hiddenDataEntity: ByteArray = encAlgorithm.encrypt(dataEntity, password)

        val dataBefore = dataMemory.subList(0, offset)
        val dataAfter = dataMemory.subList(offset + oldSize, dataMemory.size)
        val dataBetween = hiddenDataEntity.asList()

        val newSize = dataBetween.size
        val deltaSize = newSize - oldSize

        dataMemory = mutableListOf()
        dataMemory.addAll(dataBefore)
        dataMemory.addAll(dataBetween)
        dataMemory.addAll(dataAfter)

        for (i in (entityIndex + 1) until internalEntityList.size) {
            val cur = internalEntityList[i]
            internalEntityList[i] = TocEntity(cur.name, cur.contentOffset + deltaSize, cur.contentSize, cur.contentChecksum)
        }

        internalEntityList[entityIndex] = TocEntity(newName, offset, newSize, dataEntity.hashCode())
        return internalEntityList[entityIndex]
    }

    fun deleteEntity(entity: TocEntity) =
        deleteEntity(entity.name)

    fun deleteEntity(name: String) {
        val entityIndex = findByName(name)
        if (entityIndex == -1)
            throw EntityNotFoundException("Entity $name not found.")
        val entity = internalEntityList[entityIndex]
        val offset = entity.contentOffset
        val size = entity.contentSize

        val dataBefore = dataMemory.subList(0, offset)
        val dataAfter = dataMemory.subList(offset + size, dataMemory.size)
        dataMemory = mutableListOf()
        dataMemory.addAll(dataBefore)
        dataMemory.addAll(dataAfter)

        for (i in (entityIndex + 1) until internalEntityList.size) {
            val cur = internalEntityList[i]
            internalEntityList[i] = TocEntity(cur.name, cur.contentOffset - size, cur.contentSize, cur.contentChecksum)
        }

        internalEntityList.removeAt(entityIndex)
    }

    fun getResult(password: ByteArray): ByteArray {
        val encryptedContent = dataMemory

        val tableOfContent = mutableListOf<Byte>()
        internalEntityList.forEach {
            tableOfContent += it.serialize().asList()
        }
        val encryptedToc = encAlgorithm.encrypt(tableOfContent.toByteArray(), password).asList()

        val tocChecksum = tableOfContent.hashCode()
        val tocSize = encryptedToc.size
        val preamble = Preamble(author, encAlgorithmId, tocChecksum, tocSize).serialize().asList()

        val result = mutableListOf<Byte>()
        result += preamble
        result += encryptedToc
        result += encryptedContent
        println(encryptedContent.toByteArray().toHexString())
        return result.toByteArray()
    }

    companion object {
        fun read(password: ByteArray, encAlgoId: Algorithms, byteArray: ByteArray): StorageData {
            val encAlgo = encAlgoId.getAlgorithm()

            val lst = byteArray.asList()
            val preamble = Preamble.deserialize(byteArray)

            val startOfToc = preamble.size
            val sizeOfToc = preamble.tocSize
            val tocDecryptedArray = encAlgo.decrypt(lst.subList(startOfToc, startOfToc + sizeOfToc).toByteArray(), password)

            var tocPtr = 0
            val toc = mutableListOf<TocEntity>()

            while (tocPtr < tocDecryptedArray.size) {
                val curPart = tocDecryptedArray.slice(tocPtr until tocDecryptedArray.size).toByteArray()
                toc += TocEntity.deserialize(curPart)
                tocPtr += toc.last().size
            }

            val encryptedData = lst.subList(startOfToc + sizeOfToc, lst.size).toByteArray()

            val res = StorageData(preamble.author, encAlgoId)
            res.dataMemory = encryptedData.toMutableList()
            res.internalEntityList = toc

            return res
        }
    }

    private fun findByName(name: String): Int {
        return internalEntityList.indexOfFirst { it.name == name }
    }
}
