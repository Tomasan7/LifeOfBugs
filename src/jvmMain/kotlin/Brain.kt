interface Brain
{
    /**
     * Calculates [bug]'s move.
     *
     * @return The move, or `null`, if it should not move.
     */
    fun calculateMove(bug: Bug, surroundings: Surroundings): Move?
}