package me.tomasan7.lifeofbugs.game

data class Surroundings(
    val front: Tile = Tile.Space,
    val left: Tile = Tile.Space,
    val right: Tile = Tile.Space,
    val back: Tile = Tile.Space
)