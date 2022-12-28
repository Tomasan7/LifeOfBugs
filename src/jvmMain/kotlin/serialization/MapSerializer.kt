package serialization

import Tile

interface MapSerializer
{
    fun serialize(map: Array<Array<Tile>>): String
    fun deserialize(string: String): Array<Array<Tile>>
}