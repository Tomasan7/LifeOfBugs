package me.tomasan7.lifeofbugs.game

import me.tomasan7.lifeofbugs.game.Pos

class NoBugAtPosException(pos: Pos) : Exception("There is no bug at $pos!")