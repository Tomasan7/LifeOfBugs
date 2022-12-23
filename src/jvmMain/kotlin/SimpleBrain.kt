class SimpleBrain : Brain
{
    override fun calculateMove(bug: Bug, surroundings: Surroundings): Move
    {
        if (surroundings.front != null && surroundings.right == null)
            return Move.FORWARD

        if (surroundings.right != null && surroundings.left != null && surroundings.front != null)
            Move.STAY

        if (surroundings.right != null)
            Move.ROTATE_RIGHT

        return Move.ROTATE_LEFT
    }
}