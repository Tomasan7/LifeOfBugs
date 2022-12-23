data class GameConfig(
    val width: Int,
    val height: Int
)
{
    val size
        get() = width * height
}