class AggressiveBrain: Brain
{
    override fun calculateMove(bug: Bug, surroundings: Surroundings): Move
    {
        if (surroundings.front is Tile.BugTile)
            return Move.FORWARD

        if (surroundings.right is Tile.BugTile)
            return Move.ROTATE_RIGHT

        if (surroundings.left is Tile.BugTile)
            return Move.ROTATE_LEFT

        if (surroundings.back is Tile.BugTile)
            return Move.ROTATE_RIGHT

        return Move.FORWARD
    }

    override fun getBrainName() = "AggressiveBrain"
}