import androidx.compose.ui.graphics.Color

data class Bug(
    val id: Int,
    val name: String,
    val score: Int = 0,
    val brain: Brain,
    val orientation: Direction = Direction.UP,
    val color: Color
)
{
    fun rotateLeft() = copy(orientation = orientation.left())

    fun rotateRight() = copy(orientation = orientation.right())

    fun grow() = copy(score = score + 1)
}