package me.tomasan7.lifeofbugs.serialization

import me.tomasan7.lifeofbugs.game.Direction
import me.tomasan7.lifeofbugs.game.Game
import me.tomasan7.lifeofbugs.game.Tile
import me.tomasan7.lifeofbugs.game.bug.Bug
import me.tomasan7.lifeofbugs.util.randomColor
import me.tomasan7.lifeofbugs.util.randomName

/**
 * This serializer is human friendly, but only preserves bugs' positions and directions, meaning that name, color, brain and score will be lost.
 * Name, color and brain will be random and score will be 0.
 */
object BasicMapSerializer : MapSerializer
{
    override fun serialize(map: Array<Array<Tile>>): String
    {
        val builder = StringBuilder()

        for (row in map)
        {
            for (tile in row)
            {
                when (tile)
                {
                    is Tile.Void -> {}
                    is Tile.Space -> builder.append('-')
                    is Tile.Wall -> builder.append('X')
                    is Tile.BugTile ->
                    {
                        when (tile.bug.orientation)
                        {
                            Direction.UP   -> builder.append('^')
                            Direction.DOWN -> builder.append('v')
                            Direction.LEFT -> builder.append('<')
                            Direction.RIGHT -> builder.append('>')
                        }
                    }
                }
            }

            builder.append('\n')
        }

        return builder.toString()
    }

    override fun deserialize(string: String): Array<Array<Tile>>
    {
        val lines = string.lines()
        val height = lines.size
        val width = lines.maxOf { it.length }
        val map: Array<Array<Tile>> = Array(height) { Array(width) { Tile.Space } }

        for (y in 0 until height)
        {
            for (x in 0 until width)
            {
                val id = y * width + x

                map[y][x] = when (lines[y][x])
                {
                    'X' -> Tile.Wall
                    '^' -> Tile.BugTile(createBug(id, Direction.UP))
                    'v' -> Tile.BugTile(createBug(id, Direction.DOWN))
                    '<' -> Tile.BugTile(createBug(id, Direction.LEFT))
                    '>' -> Tile.BugTile(createBug(id, Direction.RIGHT))
                    else -> Tile.Space
                }
            }
        }

        return map
    }

    private fun createBug(id: Int, orientation: Direction) = Bug(
        id = id,
        name = randomName(),
        score = 0,
        brain = Game.BRAINS.random(),
        orientation = orientation,
        color = randomColor()
    )
}