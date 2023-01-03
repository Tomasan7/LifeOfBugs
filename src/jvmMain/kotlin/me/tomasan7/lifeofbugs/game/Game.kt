package me.tomasan7.lifeofbugs.game

import me.tomasan7.lifeofbugs.game.bug.AggressiveBrain
import me.tomasan7.lifeofbugs.game.bug.Bug
import me.tomasan7.lifeofbugs.game.bug.SimpleBrain
import me.tomasan7.lifeofbugs.game.Tile.Space.tile
import me.tomasan7.lifeofbugs.util.randomColor
import me.tomasan7.lifeofbugs.util.randomName

class Game
{
    constructor(gameConfig: GameConfig)
    {
        this.gameConfig = gameConfig
        this.map = Array(gameConfig.height) { Array(gameConfig.width) { Tile.Space } }
        this.allPositions = run {
            val positions = mutableListOf<Pos>()

            for (y in 0 until gameConfig.height)
                for (x in 0 until gameConfig.width)
                    positions.add(Pos(x, y))

            positions.toList()
        }
    }

    constructor(map: List<List<Tile>>)
    {
        this.gameConfig = GameConfig(map[0].size, map.size)
        this.map = map.map { it.toTypedArray() }.toTypedArray()
        this.allPositions = run {
            val positions = mutableListOf<Pos>()

            for (y in 0 until gameConfig.height)
                for (x in 0 until gameConfig.width)
                    positions.add(Pos(x, y))

            positions.toList()
        }
    }

    private val gameConfig: GameConfig
    private val map: Array<Array<Tile>>
    private val allPositions: List<Pos>

    fun getMapCopy() = map.map { it.map { tile -> if (tile is Tile.BugTile) tile.bug.copy().tile() else tile } }

    private fun setTile(pos: Pos, tile: Tile)
    {
        map[pos.y][pos.x] = tile
    }

    fun clearBugs()
    {
        for (pos in allPositions)
        {
            val tile = getTile(pos)

            if (tile is Tile.BugTile)
                setTile(pos, Tile.Space)
        }
    }

    fun setRandomBugs(amount: Int)
    {
        clearBugs()
        fillRandomBugs(amount)
    }

    fun fillRandomBugs(amount: Int)
    {
        val availablePositions = allPositions.filter { getTile(it) is Tile.Space }.toMutableList()

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

    fun fillRandomBugs()
    {
        val availablePositions = allPositions.filter { getTile(it) is Tile.Space }.toMutableList()

        availablePositions.forEachIndexed { i, availablePos ->
            setTile(
                pos = availablePos,
                tile = Bug(
                    id = i,
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
        this.map[pos.y][pos.x]
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

    fun getBugTile(bugPos: Pos) = getTile(bugPos) as? Tile.BugTile ?: throw NoBugAtPosException(bugPos)

    fun getBug(bugPos: Pos) = getBugTile(bugPos).bug

    fun tryMoveBugAndEat(bugPos: Pos, move: Move) = tryMoveBug(bugPos, move, true)

    fun tryMoveBug(bugPos: Pos, move: Move, shouldEat: Boolean)
    {
        val bug = getBug(bugPos)

        when (move)
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
            this.map[bugPos.y][bugPos.x] = Tile.Space
            this.map[destinationPos.y][destinationPos.x] = Tile.BugTile(bug)
        }

        if (shouldEat && bugAtDestination != null)
            bug.grow()
    }

    fun cycle()
    {
        val processedBugs = mutableSetOf<Bug>()
        val positionsToProcess = getBugPositions()

        for (bugPos in positionsToProcess)
        {
            try
            {
                val bug = getBug(bugPos)
                processBug(bugPos)
                processedBugs.add(bug)
            }
            catch (ignored: NoBugAtPosException)
            {
            }
        }
    }

    fun getBugPositions() = allPositions.filter { getTile(it) is Tile.BugTile }

    fun getBugs() = getBugPositions().map { getBug(it) }

    fun processBug(bugPos: Pos)
    {
        val bug = getBug(bugPos)

        val surroundings = getSurroundings(bugPos, bug.orientation)

        val move = bug.brain.calculateMove(bug, surroundings)

        if (move != null)
            tryMoveBugAndEat(bugPos, move)
    }

    companion object
    {
        val BRAINS = setOf(
            SimpleBrain(),
            AggressiveBrain(),
        )
    }
}