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
import com.onimurasame.vania.util.ext.GdxArray
import com.onimurasame.vania.util.ext.findAllRegions
import com.onimurasame.vania.util.ext.logger
import com.onimurasame.vania.util.ext.use

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
    private val attackAnimation0: Animation<TextureAtlas.AtlasRegion>
    private val attackAnimation1: Animation<TextureAtlas.AtlasRegion>
    private val attackAnimation2: Animation<TextureAtlas.AtlasRegion>

    private val attackAnimations: GdxArray<Animation<TextureAtlas.AtlasRegion>> = GdxArray()

    private var idleElapsedTime = 0f
    private var attackElapsedTime = 0f
    private var crouchingElapsedTime = 0f

    init {
        assetManager.load(CHARACTER_ATLAS, TextureAtlas::class.java)
        assetManager.finishLoading()

        playerAtlas = assetManager[CHARACTER_ATLAS]

        idleAnimation = Animation(1f / 4f, playerAtlas.findAllRegions(Player.IDLE_ANIMATION_UNARMED))
        crouchAnimation = Animation(1f /4f, playerAtlas.findAllRegions(Player.IDLE_ANIMATION_CROUCH))
        attackAnimation0 = Animation(1f / 10f, playerAtlas.findAllRegions(Player.ATTACK_ANIMATION_SWORD1))
        attackAnimation1 = Animation(1f / 10f, playerAtlas.findAllRegions(Player.ATTACK_ANIMATION_SWORD2))
        attackAnimation2 = Animation(1f / 10f, playerAtlas.findAllRegions(Player.ATTACK_ANIMATION_SWORD3))

        attackAnimations.add(attackAnimation0)
        attackAnimations.add(attackAnimation1)
        attackAnimations.add(attackAnimation2)


    }

    fun render() {
        viewport.apply()
        camera.update()

        batch.projectionMatrix = camera.combined

        batch.use {
            when(playerController.player.state) {
                Player.State.IDLE -> drawIdleAnimation()
                Player.State.MOVING -> log.debug("moving")
                Player.State.JUMPING -> log.debug("jumping")
                Player.State.STANDING_ATTACK -> drawAttackAnimation()
                Player.State.CROUCHING -> drawCrouchingAnimation()
                Player.State.CROUCHING_ATTACK -> log.debug("crouching_attack")


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
        batch.draw(attackAnimations[playerController.player.comboState].getKeyFrame(attackElapsedTime), playerController.player.x, playerController.player.y)

        if(attackAnimations[playerController.player.comboState].isAnimationFinished(attackElapsedTime)) {
            attackElapsedTime = 0f
            playerController.player.comboState++
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