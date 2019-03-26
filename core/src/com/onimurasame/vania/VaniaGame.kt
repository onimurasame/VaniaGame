package com.onimurasame.vania

import com.badlogic.gdx.Application
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.utils.Logger
import com.onimurasame.vania.screen.Gameplay

class VaniaGame : Game() {
    val assetManager = AssetManager()

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        assetManager.logger.level = Logger.DEBUG

        setScreen(Gameplay(this))
    }

    override fun dispose() {
        super.dispose()
        assetManager.dispose()
    }
}
