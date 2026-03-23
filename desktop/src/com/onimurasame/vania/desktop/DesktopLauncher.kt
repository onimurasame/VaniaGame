package com.onimurasame.vania.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.onimurasame.vania.VaniaGame
import com.onimurasame.vania.configuration.GameConfig

fun main(arg: Array<String>) {
    val config = Lwjgl3ApplicationConfiguration()

    config.setTitle("VaniaGame")
    config.setWindowedMode(GameConfig.WIDTH, GameConfig.HEIGHT)
    config.useVsync(true)
    config.setForegroundFPS(60)

    Lwjgl3Application(VaniaGame(), config)
}

