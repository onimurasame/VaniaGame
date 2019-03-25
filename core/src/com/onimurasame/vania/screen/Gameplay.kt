package com.onimurasame.vania.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.onimurasame.vania.VaniaGame
import com.onimurasame.vania.controller.PlayerController
import com.onimurasame.vania.controller.PlayerInputController
import com.onimurasame.vania.renderer.PlayerRenderer
import com.onimurasame.vania.util.ext.logger

class Gameplay(private val game: VaniaGame) : ScreenAdapter() {

    companion object {
        @JvmStatic
        private val log = logger<Gameplay>()
    }

    private val playerController = PlayerController()
    private val playerRenderer = PlayerRenderer(game.assetManager, playerController)

    override fun show() {
        log.debug("Screen loading")
        Gdx.input.inputProcessor = playerController.inputController
    }

    override fun render(delta: Float) {
        playerRenderer.render()
    }

    override fun resize(width: Int, height: Int) {
        playerRenderer.resize(width, height)
    }
}
