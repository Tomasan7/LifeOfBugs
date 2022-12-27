class SimpleBrain : Brain
{
    override fun calculateMove(bug: Bug, surroundings: Surroundings): Move?
    {
        if (surroundings.front is Tile.BugTile && surroundings.right is Tile.Space)
            return Move.FORWARD

        if (surroundings.right is Tile.BugTile && surroundings.left is Tile.BugTile && surroundings.front is Tile.BugTile)
            return null

        if (surroundings.right is Tile.BugTile)
            return Move.ROTATE_RIGHT

        return Move.ROTATE_LEFT
    }

    override fun getBrainName() = "SimpleBrain"
}