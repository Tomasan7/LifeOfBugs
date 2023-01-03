package me.tomasan7.lifeofbugs

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.application
import me.tomasan7.lifeofbugs.ui.Game

fun main() = application {
    MaterialTheme {
        Game(this)
    }
}