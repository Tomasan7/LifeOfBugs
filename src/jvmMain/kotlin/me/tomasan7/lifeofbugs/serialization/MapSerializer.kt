package me.tomasan7.lifeofbugs.serialization

import me.tomasan7.lifeofbugs.game.Tile

interface MapSerializer
{
    fun serialize(map: Array<Array<Tile>>): String
    fun deserialize(string: String): Array<Array<Tile>>
}