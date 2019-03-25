package com.onimurasame.vania.controller

import com.badlogic.gdx.InputProcessor
import com.onimurasame.vania.entity.Player
import com.onimurasame.vania.rule.isCrouching
import com.onimurasame.vania.rule.isIdle
import com.onimurasame.vania.util.ext.asKeyString
import com.onimurasame.vania.util.ext.logger

class PlayerInputController(private val player: Player) : InputProcessor {

    companion object {
        @JvmStatic
        private val log = logger<PlayerInputController>()
    }

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
        log.debug("${keycode.asKeyString()} up")
        when (keycode.asKeyString()) {
            "S" -> {
                //TODO Fix crouching being interrupted by another key release
                log.debug("Crouching key released...")
                player.state = Player.State.IDLE
            }
        }
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun keyDown(keycode: Int): Boolean {
        log.debug("${keycode.asKeyString()} down")

        if(player.isIdle()) {
            when (keycode.asKeyString()) {
                "Enter" -> {
                    log.debug("Attack key pressed...")
                    player.state = Player.State.ATTACKING
                }
                "S" -> {
                    log.debug("Crouch key pressed...")
                    player.state = Player.State.CROUCHING
                }
            }
        } else if(player.isCrouching()) {
            when (keycode.asKeyString()) {
                "Enter" -> {
                    log.debug("Attack key pressed...")
                    player.state = Player.State.ATTACKING
                }
                "S" -> {
                    log.debug("Crouch key pressed...")
                    player.state = Player.State.CROUCHING
                }
            }
        }

        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }
}
