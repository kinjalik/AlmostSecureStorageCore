package operationalComponents

import cryptography.Algorithms
import cryptography.getAlgorithm
import storedComponents.DataEntity
import storedComponents.Preamble
import storedComponents.TocEntity
import toHexString

class StorageData (
    val author: String,
    private val encAlgorithmId: Algorithms = Algorithms.AES128CBC
        ){
    private val encAlgorithm = encAlgorithmId.getAlgorithm()


    private var entityList: MutableList<TocEntity> = mutableListOf()
    private var dataMemory: MutableList<Byte> = mutableListOf()

    fun addEntity(password: ByteArray, name: String, props: Map<String, String>): Int {
        val dataEntity = DataEntity(name, props).serialize()
        val hiddenDataEntity: ByteArray = encAlgorithm.encrypt(dataEntity, password)

        val dataList = hiddenDataEntity.asList()
        dataMemory.addAll(dataList)

        val contentOffset = dataMemory.size
        val contentSize = dataList.size
        val contentChecksum = dataEntity.hashCode()

        val tocEntity = TocEntity(name, contentOffset, contentSize, contentChecksum)
        entityList.add(tocEntity)
        return entityList.size - 1
    }

    fun getResult(password: ByteArray): ByteArray {
        val encryptedContent = dataMemory

        val tableOfContent = mutableListOf<Byte>()
        entityList.forEach {
            tableOfContent += it.serialize().asList()
        }
        val encryptedToc = encAlgorithm.encrypt(tableOfContent.toByteArray(),password).asList()

        val tocChecksum = tableOfContent.hashCode()
        val tocSize = encryptedToc.size
        val preamble = Preamble(author, encAlgorithmId, tocChecksum, tocSize).serialize().asList()

        val result = mutableListOf<Byte>()
        result += preamble
        result += encryptedToc
        result += encryptedContent
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

            val res = StorageData(preamble.author,encAlgoId)
            res.dataMemory = encryptedData.toMutableList()
            res.entityList = toc

            return res
        }
    }

}