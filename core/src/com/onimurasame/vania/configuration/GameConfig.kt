package com.onimurasame.vania.configuration

object GameConfig {

    const val WIDTH = 800 // For desktop Only
    const val HEIGHT = 600 // For desktop Only

    const val WORLD_WIDTH = 1000f * 0.3f // World units
    const val WORLD_HEIGHT = 500f  * 0.3f // World Units
    const val WORLD_CENTER_Y = WORLD_HEIGHT / 2
    const val WORLD_CENTER_X = WORLD_WIDTH / 2

    const val HUD_WIDTH = 480f
    const val HUD_HEIGHT = 800f

    const val LIVES_START = 3

    const val OBSTACLE_SPAWN_TIME = 0.35f
    const val SCORE_MAX_TIME = 1.25f

    const val EASY_OBSTACLE_SPEED = 0.1f
    const val MED_OBSTACLE_SPEED = 0.15f
    const val HARD_OBTACLE_SPEED = 0.20f

}