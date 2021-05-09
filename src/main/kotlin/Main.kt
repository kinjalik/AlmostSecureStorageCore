import storedComponents.DataEntity
import cryptography.Algorithms
import operationalComponents.StorageData
import kotlin.collections.HashMap

fun main() {
    val passwd = "TEST PASSWORD EPTA".toByteArray()
    val testProps = HashMap<String, String>()
    testProps["PARAM1"] = "A"
    testProps["PARAM 2"] = "B"

    val s = StorageData("TEST AUTHOR EPTA", Algorithms.AES128CBC)
    val t = s.addEntity(passwd, "TEST ENTITY", testProps)

    val raw = s.getResult(passwd)

    val testDecrypt = StorageData.read(passwd, Algorithms.AES128CBC, raw)
    println(testDecrypt.author)
    println(testDecrypt.getEntity(passwd, t))
}

