import java.io.File
import kotlin.math.ceil

fun randomName() = namesFile.readLines().random()

fun Int.isOdd() = this % 2 != 0

fun Int.isEven() = this % 2 == 0

fun Float.ceil() = ceil(this).toInt()

fun Float.floor() = this.toInt()

private val namesFile = File(GameConfig::class.java.getResource("names.txt").path)