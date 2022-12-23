enum class Move(private val isMovement: Boolean, private val isRotation: Boolean)
{
    FORWARD(true, false),
    ROTATE_LEFT(false, true),
    ROTATE_RIGHT(false, true);

    fun isMovement() = isMovement
    fun isRotation() = isRotation
}