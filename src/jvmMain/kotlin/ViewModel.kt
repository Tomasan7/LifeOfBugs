import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

class ViewModel(val gameConfig: GameConfig, private val brain: Brain)
{
    var bugs by mutableStateOf(emptyList<Bug?>())

    init
    {
        setRandomBugs(100)
    }

    private fun setRandomBugs(amount: Int)
    {
        val newBugs = arrayOfNulls<Bug?>(gameConfig.size)

        val availableSpots = (0 until gameConfig.size).toMutableList()

        repeat(amount) {
            val availableSpotsIndex = Random.nextInt(availableSpots.size)
            val availableSpot = availableSpots[availableSpotsIndex]
            availableSpots.removeAt(availableSpotsIndex)

            newBugs[availableSpot] = Bug(
                id = it,
                name = randomName(),
                score = 0,
                orientation = Direction.random(),
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
        val newIndex: Int
        val newBug: Bug

        if (move == Move.FORWARD)
        {
            /* desiredNewIndex is null, when it's out of border. */
            val desiredNewIndex = getRelative(bugIndex, bug.orientation) ?: return

            val bugOnIndex = bugs[desiredNewIndex]
            val newScore = if (bugOnIndex == null) bug.score else bug.score + 1

            newIndex = desiredNewIndex
            newBug = bug.copy(score = newScore)
        }
        else
        {
            newIndex = bugIndex
            newBug = when (move)
            {
                Move.ROTATE_LEFT -> bug.rotateLeft()
                Move.ROTATE_RIGHT -> bug.rotateRight()
                else -> bug
            }
        }

        if (newIndex != bugIndex)
        {
            bugs = bugs.toMutableList().apply {
                this[bugIndex] = null
                this[newIndex] = newBug
            }
        }
        else
            bugs = bugs.toMutableList().apply { this[bugIndex] = newBug }
    }

    /**
     * @return the index relative to the [index] in [direction]. Or null, if it's out of border.
     */
    private fun getRelative(index: Int, direction: Direction): Int?
    {
        val destination = when (direction)
        {
            Direction.UP    -> index - gameConfig.width
            Direction.DOWN  -> index + gameConfig.width
            Direction.LEFT  -> index - 1
            Direction.RIGHT -> index + 1
        }

        val rowsBefore = index / gameConfig.width
        val rowLocalDestination = destination - rowsBefore * gameConfig.width

        val moveInBounds = when (direction)
        {
            Direction.UP, Direction.DOWN    -> destination in bugs.indices
            Direction.RIGHT, Direction.LEFT -> rowLocalDestination in 0 until gameConfig.width
        }

        if (moveInBounds)
            return destination
        else
            return null
    }

    private fun getSurroundings(bug: Bug): Surroundings
    {
        val bugIndex = bugs.indexOf(bug)

        return Surroundings(
            front = getRelative(bugIndex, bug.orientation)?.let { bugs[it] },
            right = getRelative(bugIndex, bug.orientation.right())?.let { bugs[it] },
            left = getRelative(bugIndex, bug.orientation.left())?.let { bugs[it] },
            back = getRelative(bugIndex, bug.orientation.opposite())?.let { bugs[it] }
        )
    }

    fun cycle()
    {
        val bugsIdsToProcessIterator = bugs.filterNotNull().map { it.id }.toMutableSet().iterator()

        for (bugId in bugsIdsToProcessIterator)
        {
            val bug = bugs.find { it?.id == bugId } ?: continue

            val surroundings = getSurroundings(bug)

            val move = brain.calculateMove(bug, surroundings)

            if (move != null)
                moveBugAndEat(bug, move)

            bugsIdsToProcessIterator.remove()
        }
    }
}