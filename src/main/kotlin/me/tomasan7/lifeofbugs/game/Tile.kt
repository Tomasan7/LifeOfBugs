package me.tomasan7.lifeofbugs.game

import me.tomasan7.lifeofbugs.game.bug.Bug

sealed interface Tile
{
    data class BugTile(val bug: Bug) : Tile
    object Void : Tile
    object Space : Tile
    object Wall : Tile

    fun Bug.tile() = BugTile(this)

    companion object
    {
        fun bugOrElseSpace(bug: Bug?) = if (bug != null) BugTile(bug) else Space
    }
}