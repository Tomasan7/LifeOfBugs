package me.tomasan7.lifeofbugs.serialization

import me.tomasan7.lifeofbugs.game.Tile

interface MapSerializer
{
    fun serialize(map: List<List<Tile>>): String
    fun deserialize(string: String): List<List<Tile>>
}