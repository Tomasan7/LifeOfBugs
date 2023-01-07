package me.tomasan7.lifeofbugs.util

import androidx.compose.ui.graphics.Color
import kotlin.math.ceil
import kotlin.random.Random

fun randomName() = names.random()

fun randomColor() = Color(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))

fun Int.isOdd() = this % 2 != 0

fun Int.isEven() = this % 2 == 0

fun Float.ceil() = ceil(this).toInt()

fun Float.floor() = this.toInt()

private val names = Thread.currentThread().contextClassLoader.getResource("names.txt").readText().split("\n")