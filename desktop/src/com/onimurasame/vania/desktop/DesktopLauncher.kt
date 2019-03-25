package com.onimurasame.vania.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.onimurasame.vania.VaniaGame
import com.onimurasame.vania.configuration.GameConfig

class DesktopLauncher {}

fun main(arg: Array<String>) {
    val config = LwjglApplicationConfiguration()

    config.width = GameConfig.WIDTH
    config.height = GameConfig.HEIGHT

    LwjglApplication(VaniaGame(), config)
}

