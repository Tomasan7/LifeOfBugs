data class Pos(val x: Int, val y: Int)
{
    fun left() = Pos(x - 1, y)
    fun right() = Pos(x + 1, y)
    fun up() = Pos(x, y + 1)
    fun down() = Pos(x, y - 1)

    fun getRelative(direction: Direction) = when (direction)
    {
        Direction.UP    -> up()
        Direction.DOWN  -> down()
        Direction.LEFT  -> left()
        Direction.RIGHT -> right()
    }
}