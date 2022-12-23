import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

class ViewModel(val gameConfig: GameConfig)
{
    var bugs by mutableStateOf(emptyList<Bug?>())

    init
    {
        setRandomBugs(5)
    }

    private fun setRandomBugs(amount: Int)
    {
        val newBugs = arrayOfNulls<Bug?>(gameConfig.size)

        val availableIndexes = (0 until gameConfig.size).toList()

        repeat(amount) {
            val availableIndexesIndex = Random.nextInt(gameConfig.size)
            val availableIndex = availableIndexes[availableIndexesIndex]

            newBugs[availableIndex] = Bug(
                id = it,
                name = randomName(),
                score = 1,
                orientation = Orientation.random(),
                color = Color(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
            )
        }

        bugs = newBugs.toList()
    }

    fun updateBug(bug: Bug, newBug: Bug)
    {
        val bugIndex = bugs.indexOf(bug)
        bugs = bugs.toMutableList().apply { this[bugIndex] = newBug }
    }

    fun moveBugAndEat(bug: Bug, move: Move)
    {
        val bugIndex = bugs.indexOf(bug)

        val newBugIndex = when (move)
        {
            Move.FORWARD -> when (bug.orientation)
            {
                Orientation.UP -> bugIndex - gameConfig.width
                Orientation.DOWN -> bugIndex + gameConfig.width
                Orientation.LEFT -> bugIndex - 1
                Orientation.RIGHT -> bugIndex + 1
            }
            Move.ROTATE_LEFT -> bugIndex
            Move.ROTATE_RIGHT -> bugIndex
        }

        val bugOnLocation = bugs[newBugIndex]

        val newSize = if (bugOnLocation != null) bug.score + 1 else bug.score

        val newBug = bug.copy(
            score = newSize,
            orientation = bug.orientation
        )

        val newBugs = bugs.toMutableList()

        newBugs[bugIndex] = null
        newBugs[newBugIndex] = newBug

        bugs = newBugs
    }
}