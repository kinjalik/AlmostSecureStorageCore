import cryptography.Algorithms
import operationalComponents.StorageData
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class DataManipulationTests {
    val authorName = "Albert Akmukhametov"
    val password = "Test passowrd phrase".toByteArray()

    val propsForEntities = listOf(
        mapOf(
            Pair("Entity 1, parameter 1", "Value of parameter 1"),
            Pair("Entity 1, parameter 2", "Value of parameter 2"),
            Pair("Entity 1, parameter 3", "Value of parameter 3"),
            Pair("Entity 1, parameter 4", "Value of parameter 4"),
        ),
        mapOf(
            Pair("Entity 2, parameter 1", "Value of parameter 1"),
            Pair("Entity 2, parameter 2", "Value of parameter 2"),
            Pair("Entity 2, parameter 3", "Value of parameter 3"),
            Pair("Entity 2, parameter 4", "Value of parameter 4"),
        )
    )
    val entityNames = listOf("Entity One", "Entity Two")

    @Test
    fun `Database Creation - AES128CBC`() {
        val database = StorageData(authorName, Algorithms.AES128CBC)
        assertEquals(database.author, authorName)
    }

    // CRud
    @Test
    fun `Entity addition operation`() {
        val storage = StorageData(authorName, Algorithms.AES128CBC)
        for (i in propsForEntities.indices) {
            storage.addEntity(password, entityNames[i], propsForEntities[i])
        }

        for ((counter, tocEntity) in storage.entities.withIndex()) {
            val dataEntity = storage.getEntity(password, tocEntity)

            val entityName = dataEntity.name
            val entityProps = dataEntity.properties

            assertEquals(entityName, entityNames[counter])
            assertEquals(entityProps, propsForEntities[counter])
        }
    }

    // crUd
    @Test
    fun `Entity update operation`() {
        val storage = StorageData(authorName, Algorithms.AES128CBC)
        for (i in propsForEntities.indices) {
            storage.addEntity(password, "$i", mapOf())
        }

        for ((i, entity) in storage.entities.withIndex()) {
            storage.updateEntity(password, entity, entityNames[i], propsForEntities[i])
        }
        println("GAOFJA")
        for ((counter, tocEntity) in storage.entities.withIndex()) {
            val dataEntity = storage.getEntity(password, tocEntity)

            val entityName = dataEntity.name
            val entityProps = dataEntity.properties

            assertEquals(entityName, entityNames[counter])
            assertEquals(entityProps, propsForEntities[counter])
        }
    }

    // cruD
    @Test
    fun `Entity delete operation`() {
        val storage = StorageData(authorName, Algorithms.AES128CBC)
        for (i in propsForEntities.indices) {
            storage.addEntity(password, entityNames[i], propsForEntities[i])
        }

        // Delete first
        storage.deleteEntity(entityNames[0])

        for ((counter, tocEntity) in storage.entities.withIndex()) {
            val dataEntity = storage.getEntity(password, tocEntity)

            val entityName = dataEntity.name
            val entityProps = dataEntity.properties

            assertEquals(entityName, entityNames[counter + 1])
            assertEquals(entityProps, propsForEntities[counter + 1])
        }
    }
}
