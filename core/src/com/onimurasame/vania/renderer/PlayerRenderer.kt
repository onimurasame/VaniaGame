package com.onimurasame.vania.renderer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.onimurasame.vania.configuration.GameConfig
import com.onimurasame.vania.controller.PlayerController
import com.onimurasame.vania.entity.Player
import com.onimurasame.vania.util.ext.*

class PlayerRenderer(assetManager: AssetManager, private val playerController: PlayerController) : Disposable {

    companion object {
        @JvmStatic
        private val log = logger<PlayerRenderer>()

        private const val CHARACTER_ATLAS = "player/player.atlas"
    }

    private val camera = OrthographicCamera()
    private val viewport = FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera)
    private val batch: SpriteBatch = SpriteBatch()

    private val playerAtlas: TextureAtlas

    private val idleAnimation: Animation<TextureAtlas.AtlasRegion>
    private val crouchAnimation: Animation<TextureAtlas.AtlasRegion>
    private val attackAnimation: Animation<TextureAtlas.AtlasRegion>

    private var idleElapsedTime = 0f
    private var attackElapsedTime = 0f
    private var crouchingElapsedTime = 0f

    init {
        assetManager.load(CHARACTER_ATLAS, TextureAtlas::class.java)
        assetManager.finishLoading()

        playerAtlas = assetManager[CHARACTER_ATLAS]

        idleAnimation = Animation(1f / 4f, playerAtlas.findAllRegions(Player.IDLE_ANIMATION_UNARMED))
        crouchAnimation = Animation(1f /4f, playerAtlas.findAllRegions(Player.IDLE_ANIMATION_CROUCH))
        attackAnimation = Animation(1f / 10f, playerAtlas.findAllRegions(Player.ATTACK_ANIMATION_UNARMED))


    }

    fun render() {
        viewport.apply()
        camera.update()
        clearScreen()

        batch.projectionMatrix = camera.combined

        batch.use {
            when(playerController.player.state) {
                Player.State.IDLE -> drawIdleAnimation()
                Player.State.CROUCHING -> drawCrouchingAnimation()
                Player.State.ATTACKING -> drawAttackAnimation()

            }
        }
    }



    private fun drawIdleAnimation() {
        idleElapsedTime += Gdx.graphics.deltaTime
        batch.draw(idleAnimation.getKeyFrame(idleElapsedTime, true), playerController.player.x, playerController.player.y)
    }

    private fun drawCrouchingAnimation() {
        crouchingElapsedTime += Gdx.graphics.deltaTime
        batch.draw(crouchAnimation.getKeyFrame(crouchingElapsedTime, true), playerController.player.x, playerController.player.y)
    }

    private fun drawAttackAnimation() {

        batch.draw(attackAnimation.getKeyFrame(attackElapsedTime), playerController.player.x, playerController.player.y)

        if(attackAnimation.isAnimationFinished(attackElapsedTime)) {
            attackElapsedTime = 0f
            playerController.player.state = Player.State.IDLE
        } else {
            attackElapsedTime += Gdx.graphics.deltaTime
        }
    }

    fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {

    }

}