package com.onimurasame.vania.configuration

import com.badlogic.gdx.math.Rectangle

object LevelGeometry {
    private const val TILE_SIZE = 16f
    private const val FLOOR_Y = -TILE_SIZE

    const val LEVEL_WIDTH = GameConfig.WORLD_WIDTH * 3f
    const val LEVEL_HEIGHT = GameConfig.WORLD_HEIGHT * 1.5f

    const val SPAWN_X = TILE_SIZE * 3
    const val SPAWN_Y = TILE_SIZE * 2

    val solids: List<Rectangle> = buildList {
        // Floor tile strip with top aligned at y = 0.
        val floorTileCount = (LEVEL_WIDTH / TILE_SIZE).toInt() + 2
        for (tile in 0 until floorTileCount) {
            add(Rectangle(tile * TILE_SIZE, FLOOR_Y, TILE_SIZE, TILE_SIZE))
        }

        // Small elevated platforms for early movement testing.
        addPlatform(startTileX = 8, endTileX = 12, tileY = 4)
        addPlatform(startTileX = 15, endTileX = 19, tileY = 7)
        addPlatform(startTileX = 28, endTileX = 34, tileY = 5)
        addPlatform(startTileX = 40, endTileX = 48, tileY = 8)
    }

    private fun MutableList<Rectangle>.addPlatform(startTileX: Int, endTileX: Int, tileY: Int) {
        for (tileX in startTileX..endTileX) {
            add(Rectangle(tileX * TILE_SIZE, tileY * TILE_SIZE, TILE_SIZE, TILE_SIZE))
        }
    }
}
