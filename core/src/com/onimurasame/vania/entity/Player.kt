package com.onimurasame.vania.entity

import com.onimurasame.vania.util.ext.logger

class Player {

    companion object {
        @JvmStatic
        private val log = logger<Player>()

        const val IDLE_ANIMATION_UNARMED = "adventurer-idle-0"
        const val IDLE_ANIMATION_ARMED = "adventurer-idle-2"
        const val IDLE_ANIMATION_CROUCH = "adventurer-crouch-0"
        const val RUN_ANIMATION = "adventurer-run"
        const val JUMP_ANIMATION = "adventurer-jump"
        const val FALL_ANIMATION = "adventurer-fall"
        const val ATTACK_ANIMATION_SWORD1 = "adventurer-attack1"
        const val ATTACK_ANIMATION_SWORD2 = "adventurer-attack2"
        const val ATTACK_ANIMATION_SWORD3 = "adventurer-attack3"
    }

    var x: Float = 0f
    var y: Float = 0f
    var velocityX: Float = 0f
    var velocityY: Float = 0f
    var moveAxis: Float = 0f
    var jumpHeld: Boolean = false
    var crouchHeld: Boolean = false
    var jumpBuffered: Boolean = false
    var isGrounded: Boolean = true
    var coyoteTimer: Float = 0f
    var jumpBufferTimer: Float = 0f
    var facingRight: Boolean = true

    var state: State = State.IDLE
        set(value) {
            log.debug("Changing from $field to $value")
            field = value
            log.debug("Now $field")
        }

    var comboState: Int = 0
        set(value) {
            field = if (value > 2) { 0 } else { value }
        }

    fun queueJump() {
        jumpBuffered = true
    }

    fun consumeQueuedJump(): Boolean {
        if (!jumpBuffered) {
            return false
        }
        jumpBuffered = false
        return true
    }

    enum class State {
        IDLE,
        STANDING_ATTACK,
        MOVING,
        JUMPING,
        CROUCHING,
        CROUCHING_ATTACK
    }

}