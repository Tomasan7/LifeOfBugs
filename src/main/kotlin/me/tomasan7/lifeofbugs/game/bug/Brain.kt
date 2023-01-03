package me.tomasan7.lifeofbugs.game.bug

import me.tomasan7.lifeofbugs.game.Move
import me.tomasan7.lifeofbugs.game.Surroundings

interface Brain
{
    /**
     * Calculates [bug]'s move.
     *
     * @return The move, or `null`, if it should not move.
     */
    fun calculateMove(bug: Bug, surroundings: Surroundings): Move?

    fun getBrainName(): String
}