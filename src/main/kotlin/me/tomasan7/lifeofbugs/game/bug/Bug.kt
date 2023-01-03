package me.tomasan7.lifeofbugs.game.bug

import androidx.compose.ui.graphics.Color
import me.tomasan7.lifeofbugs.game.Direction

data class Bug(
    val id: Int,
    val name: String,
    var score: Int = 0,
    val brain: Brain,
    var orientation: Direction = Direction.UP,
    val color: Color
)
{
    fun rotateLeft()
    {
        orientation = orientation.left()
    }

    fun rotateRight()
    {
        orientation = orientation.right()
    }

    fun grow()
    {
        score++
    }
}