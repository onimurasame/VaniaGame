package com.onimurasame.vania.controller

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Input
import com.onimurasame.vania.entity.Player
import com.onimurasame.vania.rule.isCrouching
import com.onimurasame.vania.rule.isIdle
import com.onimurasame.vania.rule.wasCrouching
import com.onimurasame.vania.util.ext.logger

class PlayerInputController(private val player: Player) : InputProcessor {

    companion object {
        @JvmStatic
        private val log = logger<PlayerInputController>()
    }

    private var leftPressed = false
    private var rightPressed = false

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun scrolled(amount: Int): Boolean {
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        log.debug("${Input.Keys.toString(keycode)} up")
        when (keycode) {
            Input.Keys.A, Input.Keys.LEFT -> leftPressed = false
            Input.Keys.D, Input.Keys.RIGHT -> rightPressed = false
            Input.Keys.W, Input.Keys.SPACE, Input.Keys.UP -> player.jumpHeld = false
            Input.Keys.S, Input.Keys.DOWN -> {
                player.crouchHeld = false
                log.debug("Crouching key released...")
                if (player.isGrounded && player.state == Player.State.CROUCHING) {
                    player.state = Player.State.IDLE
                }
            }
            Input.Keys.ENTER -> {
                if(player.wasCrouching()) {
                    log.debug("Crouching Attack Released")
                    player.state = Player.State.CROUCHING
                }
            }
        }
        updateMoveAxis()
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun keyDown(keycode: Int): Boolean {
        log.debug("${Input.Keys.toString(keycode)} down")

        when (keycode) {
            Input.Keys.A, Input.Keys.LEFT -> leftPressed = true
            Input.Keys.D, Input.Keys.RIGHT -> rightPressed = true
            Input.Keys.W, Input.Keys.SPACE, Input.Keys.UP -> {
                if (!player.jumpHeld) {
                    player.queueJump()
                }
                player.jumpHeld = true
            }
            Input.Keys.S, Input.Keys.DOWN -> player.crouchHeld = true
        }
        updateMoveAxis()

        if(player.isIdle()) {
            when (keycode) {
                Input.Keys.ENTER -> {
                    log.debug("Attack key pressed...")
                    player.state = Player.State.STANDING_ATTACK
                }
                Input.Keys.S, Input.Keys.DOWN -> {
                    log.debug("Crouch key pressed...")
                    player.state = Player.State.CROUCHING
                }
            }
        } else if(player.isCrouching()) {
            when (keycode) {
                Input.Keys.ENTER -> {
                    log.debug("Attack key pressed...")
                    //player.state = Player.State.CROUCHING_ATTACK
                }
                Input.Keys.S, Input.Keys.DOWN -> {
                    log.debug("Crouch key pressed...")
                    player.state = Player.State.CROUCHING
                }
            }
        }

        return true
    }

    private fun updateMoveAxis() {
        player.moveAxis = when {
            leftPressed && !rightPressed -> -1f
            rightPressed && !leftPressed -> 1f
            else -> 0f
        }
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }
}
