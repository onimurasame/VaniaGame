package com.onimurasame.vania.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.onimurasame.vania.VaniaGame
import com.onimurasame.vania.configuration.GameConfig

class MainMenuScreen(private val game: VaniaGame) : ScreenAdapter() {

    private val camera = OrthographicCamera()
    private val viewport = FitViewport(GameConfig.WIDTH.toFloat(), GameConfig.HEIGHT.toFloat(), camera)
    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val font = BitmapFont()

    private val menuItems = arrayOf("Start Game", "Exit")
    private var selectedIndex = 0

    override fun show() {
        camera.setToOrtho(false, GameConfig.WIDTH.toFloat(), GameConfig.HEIGHT.toFloat())
        font.color = Color.WHITE
        font.data.setScale(1.2f)
    }

    override fun render(delta: Float) {
        handleInput()

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.08f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        viewport.apply()
        camera.update()

        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.12f, 0.12f, 0.18f, 1f)
        shapeRenderer.rect(180f, 120f, 440f, 360f)

        for (i in menuItems.indices) {
            if (i == selectedIndex) {
                shapeRenderer.color = Color(0.80f, 0.1f, 0.1f, 1f)
                shapeRenderer.rect(230f, 300f - i * 80f, 20f, 20f)
            } else {
                shapeRenderer.color = Color(0.35f, 0.35f, 0.45f, 1f)
                shapeRenderer.rect(230f, 300f - i * 80f, 20f, 20f)
            }
        }
        shapeRenderer.end()

        batch.projectionMatrix = camera.combined
        batch.begin()
        font.draw(batch, "VaniaGame MVP", 300f, 430f)
        font.draw(batch, "Use W/S or Up/Down and Enter", 230f, 390f)
        font.draw(batch, menuItems[0], 270f, 315f)
        font.draw(batch, menuItems[1], 270f, 235f)
        batch.end()
    }

    private fun handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedIndex = (selectedIndex + menuItems.size - 1) % menuItems.size
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedIndex = (selectedIndex + 1) % menuItems.size
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (selectedIndex == 0) {
                game.screen = Gameplay(game)
            } else {
                Gdx.app.exit()
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit()
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        shapeRenderer.dispose()
        batch.dispose()
        font.dispose()
    }
}
