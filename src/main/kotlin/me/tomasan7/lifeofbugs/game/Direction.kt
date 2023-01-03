package me.tomasan7.lifeofbugs.game

enum class Direction(val degrees: Int)
{
    UP(0),
    LEFT(90),
    DOWN(180),
    RIGHT(270);

    fun left() = when (this)
    {
        UP    -> LEFT
        RIGHT -> UP
        DOWN  -> RIGHT
        LEFT  -> DOWN
    }

    fun right() = when (this)
    {
        UP    -> RIGHT
        RIGHT -> DOWN
        DOWN  -> LEFT
        LEFT  -> UP
    }

    fun opposite() = when (this)
    {
        UP    -> DOWN
        RIGHT -> LEFT
        DOWN  -> UP
        LEFT  -> RIGHT
    }

    companion object
    {
        fun random() = values().random()
    }
}