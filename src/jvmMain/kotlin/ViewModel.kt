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
        setRandomBugs(50)
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
                score = 0,
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

        val desiredDestination = when (move)
        {
            Move.FORWARD -> when (bug.orientation)
            {
                Orientation.UP    -> bugIndex - gameConfig.width
                Orientation.DOWN  -> bugIndex + gameConfig.width
                Orientation.LEFT  -> bugIndex - 1
                Orientation.RIGHT -> bugIndex + 1
            }

            Move.ROTATE_LEFT,
            Move.ROTATE_RIGHT,
            Move.STAY    -> bugIndex
        }

        val rowsBefore = bugIndex / gameConfig.width
        val rowLocalDesiredDestination = desiredDestination - rowsBefore * gameConfig.width

        val movePossible = when
        {
            move == Move.FORWARD && (bug.orientation == Orientation.UP || bug.orientation == Orientation.DOWN)    -> desiredDestination in bugs.indices
            move == Move.FORWARD && (bug.orientation == Orientation.RIGHT || bug.orientation == Orientation.LEFT)  -> rowLocalDesiredDestination in 0 until gameConfig.width
            else -> true
        }

        if (!movePossible)
            return

        val bugOnLocation = bugs[desiredDestination]

        val newSize = if (bugOnLocation != null) bug.score + 1 else bug.score

        val newBug = bug.copy(
            score = newSize,
            orientation = bug.orientation
        )

        val newBugs = bugs.toMutableList()

        newBugs[bugIndex] = null
        newBugs[desiredDestination] = newBug

        bugs = newBugs
    }

    fun getSurroundings(bug: Bug): Surroundings
    {
        val bugIndex = bugs.indexOf(bug)

        val northIndex = bugIndex - gameConfig.width
        val eastIndex = bugIndex + 1
        val southIndex = bugIndex + gameConfig.width
        val westIndex = bugIndex - 1

        val frontIndex = when (bug.orientation)
        {
            Orientation.UP -> northIndex
            Orientation.DOWN -> southIndex
            Orientation.LEFT -> westIndex
            Orientation.RIGHT -> eastIndex
        }

        val rightIndex = when (bug.orientation)
        {
            Orientation.UP -> eastIndex
            Orientation.DOWN -> westIndex
            Orientation.LEFT -> northIndex
            Orientation.RIGHT -> southIndex
        }

        val leftIndex = when (bug.orientation)
        {
            Orientation.UP -> westIndex
            Orientation.DOWN -> eastIndex
            Orientation.LEFT -> southIndex
            Orientation.RIGHT -> northIndex
        }

        val backIndex = when (bug.orientation)
        {
            Orientation.UP -> southIndex
            Orientation.DOWN -> northIndex
            Orientation.LEFT -> eastIndex
            Orientation.RIGHT -> westIndex
        }

        return Surroundings(
            front = bugs.getOrNull(frontIndex),
            right = bugs.getOrNull(rightIndex),
            left = bugs.getOrNull(leftIndex),
            back = bugs.getOrNull(backIndex)
        )
    }
}