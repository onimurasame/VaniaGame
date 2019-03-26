package com.onimurasame.vania.renderer

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.onimurasame.vania.configuration.GameConfig
import com.onimurasame.vania.controller.PlayerController
import com.onimurasame.vania.util.ext.GdxArray
import com.onimurasame.vania.util.ext.logger
import com.onimurasame.vania.util.ext.use

class BackgroundRenderer(assetManager: AssetManager, private val playerController: PlayerController) : Disposable {

    companion object {
        @JvmStatic
        private val log = logger<BackgroundRenderer>()
        private const val BACKGROUND_ATLAS = "backgrounds/forest/forest_background.atlas"
    }

    private val camera = OrthographicCamera()
    private val viewport = FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera)
    private val batch: SpriteBatch = SpriteBatch()

    private val backgroundAtlas: TextureAtlas

    private val backgroundLayers: GdxArray<TextureAtlas.AtlasRegion>


    init {
        assetManager.load(BACKGROUND_ATLAS, TextureAtlas::class.java)
        assetManager.finishLoading()

        backgroundAtlas = assetManager[BACKGROUND_ATLAS]

        backgroundLayers = backgroundAtlas.regions

    }

    fun render() {
        viewport.apply()
        camera.update()

        batch.projectionMatrix = camera.combined

        batch.use {
            backgroundLayers.forEach { backgroundLayer: TextureAtlas.AtlasRegion ->
                batch.draw(backgroundLayer, 0f, 0f)
            }
        }

    }

    fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {

    }

}