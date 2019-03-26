package com.onimurasame.vania.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.onimurasame.vania.VaniaGame
import com.onimurasame.vania.controller.PlayerController
import com.onimurasame.vania.renderer.BackgroundRenderer
import com.onimurasame.vania.renderer.PlayerRenderer
import com.onimurasame.vania.util.ext.clearScreen
import com.onimurasame.vania.util.ext.logger

class Gameplay(game: VaniaGame) : ScreenAdapter() {

    companion object {
        @JvmStatic
        private val log = logger<Gameplay>()
    }

    private val playerController = PlayerController()
    private val playerRenderer = PlayerRenderer(game.assetManager, playerController)
    private val backgroundRenderer = BackgroundRenderer(game.assetManager, playerController)

    override fun show() {
        log.debug("Screen loading")
        Gdx.input.inputProcessor = playerController.inputController


    }

    override fun render(delta: Float) {
        clearScreen()
        backgroundRenderer.render()
        playerRenderer.render()
    }

    override fun resize(width: Int, height: Int) {
        playerRenderer.resize(width, height)
        backgroundRenderer.resize(width, height)
    }

    override fun dispose() {

    }
}
