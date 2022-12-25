class SimpleBrain : Brain
{
    override fun calculateMove(bug: Bug, surroundings: Surroundings): Move?
    {
        if (surroundings.front != null && surroundings.right == null)
            return Move.FORWARD

        if (surroundings.right != null && surroundings.left != null && surroundings.front != null)
            return null

        if (surroundings.right != null)
            return Move.ROTATE_RIGHT

        return Move.ROTATE_LEFT
    }

    override fun getBrainName() = "SimpleBrain"
}