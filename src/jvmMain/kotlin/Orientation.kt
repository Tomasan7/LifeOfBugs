enum class Orientation(val degrees: Int)
{
    UP(0),
    RIGHT(90),
    DOWN(180),
    LEFT(270);

    fun left(): Orientation
    {
        return when (this)
        {
            UP -> LEFT
            RIGHT -> UP
            DOWN -> RIGHT
            LEFT -> DOWN
        }
    }

    fun right(): Orientation
    {
        return when (this)
        {
            UP -> RIGHT
            RIGHT -> DOWN
            DOWN -> LEFT
            LEFT -> UP
        }
    }

    companion object
    {
        fun random() = values().random()
    }
}