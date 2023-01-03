package me.tomasan7.lifeofbugs.util

import androidx.compose.ui.graphics.Color
import me.tomasan7.lifeofbugs.game.GameConfig
import java.io.File
import kotlin.math.ceil
import kotlin.random.Random

fun randomName() = namesFile.readLines().random()

fun randomColor() = Color(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))

fun Int.isOdd() = this % 2 != 0

fun Int.isEven() = this % 2 == 0

fun Float.ceil() = ceil(this).toInt()

fun Float.floor() = this.toInt()

private val namesFile = File(Thread.currentThread().contextClassLoader.getResource("names.txt").file)