class AggressiveBrain: Brain
{
    override fun calculateMove(bug: Bug, surroundings: Surroundings): Move
    {
        if (surroundings.front != null)
            return Move.FORWARD

        if (surroundings.right != null)
            return Move.ROTATE_RIGHT

        if (surroundings.left != null)
            return Move.ROTATE_LEFT

        if (surroundings.back != null)
            return Move.ROTATE_RIGHT

        return Move.FORWARD
    }

    override fun getBrainName() = "AggressiveBrain"
}