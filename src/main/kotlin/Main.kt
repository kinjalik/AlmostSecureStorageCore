import kotlin.experimental.and

fun main() {
    val t = UnencryptedTOC(1, 10)
    println(t.serialize().toHexString())
}

