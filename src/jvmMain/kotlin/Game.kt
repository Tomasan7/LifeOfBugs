import Tile.Space.tile

class Game(private val gameConfig: GameConfig)
{
    private val map: Array<Array<Tile>> = Array(gameConfig.width) { Array(gameConfig.height) { Tile.Space } }

    fun getMap() = map.map { it.toList() }

    private fun setTile(pos: Pos, tile: Tile)
    {
        map[pos.x][pos.y] = tile
    }

    fun setRandomBugs(amount: Int)
    {
        val availablePositions = mutableSetOf(
            *map.indices.flatMap { x -> map[x].indices.filter { map[x][it] is Tile.Space }.map { y -> Pos(x, y) } }.toTypedArray()
        )

        repeat(amount) {
            val availablePos = availablePositions.random()
            availablePositions.remove(availablePos)

            setTile(
                pos = availablePos,
                tile = Bug(
                    id = it,
                    name = randomName(),
                    score = 0,
                    orientation = Direction.random(),
                    brain = BRAINS.random(),
                    color = randomColor()
                ).tile())
        }
    }

    fun getTile(pos: Pos) = try
    {
        this.map[pos.x][pos.y]
    }
    catch (e: IndexOutOfBoundsException)
    {
        Tile.Void
    }

    fun getRelative(pos: Pos, direction: Direction) = getTile(pos.getRelative(direction))

    fun getSurroundings(pos: Pos, direction: Direction) = Surroundings(
        getTile(pos.getRelative(direction)),
        getTile(pos.getRelative(direction.left())),
        getTile(pos.getRelative(direction.right())),
        getTile(pos.getRelative(direction.opposite()))
    )

    fun isMovePossible(pos: Pos, direction: Direction, canEat: Boolean): Boolean
    {
        val destination = getRelative(pos, direction)

        if (destination is Tile.Space)
            return true

        if (destination is Tile.BugTile && canEat)
            return true

        return false
    }

    fun isMovePossibleWhileEating(pos: Pos, direction: Direction) = isMovePossible(pos, direction, true)

    fun getBugTile(bugPos: Pos) = getTile(bugPos) as? Tile.BugTile ?: throw IllegalArgumentException("There is no bug at $bugPos")

    fun getBug(bugPos: Pos) = getBugTile(bugPos).bug

    fun tryMoveBugAndEat(bugPos: Pos, move: Move) = tryMoveBug(bugPos, move, true)

    fun tryMoveBug(bugPos: Pos, move: Move, shouldEat: Boolean)
    {
        val bug = getBug(bugPos)

        when(move)
        {
            Move.ROTATE_LEFT  -> bug.rotateLeft()
            Move.ROTATE_RIGHT -> bug.rotateRight()
            Move.FORWARD      -> tryMoveBugForward(bugPos, shouldEat)
        }
    }

    fun tryMoveBugForward(bugPos: Pos, shouldEat: Boolean)
    {
        val bug = getBug(bugPos)
        val direction = bug.orientation

        val destinationPos = bugPos.getRelative(direction)
        val destinationTile = getTile(destinationPos)
        val bugAtDestination = (destinationTile as? Tile.BugTile)?.bug

        if (destinationTile is Tile.Space || destinationTile is Tile.BugTile)
        {
            this.map[bugPos.x][bugPos.y] = Tile.Space
            this.map[destinationPos.x][destinationPos.y] = Tile.BugTile(bug)
        }

        if (shouldEat && bugAtDestination != null)
            bug.grow()
    }

    companion object
    {
        val BRAINS = setOf(
            SimpleBrain(),
            AggressiveBrain(),
        )
    }
}