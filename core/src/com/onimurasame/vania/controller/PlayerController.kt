package com.onimurasame.vania.controller

import com.onimurasame.vania.entity.Player
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class PlayerController {

    companion object {
        private const val WALK_SPEED = 1.25f * 60f
        private const val GROUND_ACCELERATION = 25f * 60f
        private const val GROUND_DECELERATION = 35f * 60f
        private const val JUMP_VELOCITY = 5.4f * 60f
        private const val GRAVITY_UP = 22f * 60f
        private const val GRAVITY_DOWN = 30f * 60f
        private const val TERMINAL_FALL_SPEED = -12f * 60f
        private const val JUMP_RELEASE_MULTIPLIER = 0.45f
        private const val COYOTE_TIME_SEC = 0.10f
        private const val JUMP_BUFFER_SEC = 0.10f
        private const val GROUND_Y = 0f
    }

    var player = Player()
    var inputController = PlayerInputController(player)

    fun update(delta: Float) {
        val dt = min(delta, 1f / 30f)

        if (player.consumeQueuedJump()) {
            player.jumpBufferTimer = JUMP_BUFFER_SEC
        } else {
            player.jumpBufferTimer = max(0f, player.jumpBufferTimer - dt)
        }

        if (player.isGrounded) {
            player.coyoteTimer = COYOTE_TIME_SEC
        } else {
            player.coyoteTimer = max(0f, player.coyoteTimer - dt)
        }

        val movementLocked = player.state == Player.State.STANDING_ATTACK || player.state == Player.State.CROUCHING_ATTACK
        val canMove = !movementLocked && player.state != Player.State.CROUCHING

        if (canMove) {
            val targetSpeed = player.moveAxis * WALK_SPEED
            val speedStep = if (abs(targetSpeed) > 0.001f) GROUND_ACCELERATION else GROUND_DECELERATION
            player.velocityX = moveTowards(player.velocityX, targetSpeed, speedStep * dt)
        } else {
            player.velocityX = moveTowards(player.velocityX, 0f, GROUND_DECELERATION * dt)
        }

        val canJumpNow = player.jumpBufferTimer > 0f && player.coyoteTimer > 0f && !movementLocked
        if (canJumpNow) {
            player.velocityY = JUMP_VELOCITY
            player.isGrounded = false
            player.jumpBufferTimer = 0f
            player.coyoteTimer = 0f
            if (player.state != Player.State.STANDING_ATTACK && player.state != Player.State.CROUCHING_ATTACK) {
                player.state = Player.State.JUMPING
            }
        }

        if (!player.isGrounded) {
            val gravity = if (player.velocityY > 0f) GRAVITY_UP else GRAVITY_DOWN
            player.velocityY -= gravity * dt

            if (!player.jumpHeld && player.velocityY > 0f) {
                player.velocityY *= JUMP_RELEASE_MULTIPLIER
            }

            player.velocityY = max(TERMINAL_FALL_SPEED, player.velocityY)
        }

        player.x += player.velocityX * dt
        player.y += player.velocityY * dt

        if (player.y <= GROUND_Y) {
            player.y = GROUND_Y
            if (player.velocityY < 0f) {
                player.velocityY = 0f
            }
            player.isGrounded = true
        } else {
            player.isGrounded = false
        }

        if (abs(player.velocityX) > 0.001f) {
            player.facingRight = player.velocityX > 0f
        }

        if (player.state == Player.State.CROUCHING && !player.crouchHeld && player.isGrounded) {
            player.state = Player.State.IDLE
        } else if (player.crouchHeld && player.isGrounded && !movementLocked) {
            player.state = Player.State.CROUCHING
        } else if (!movementLocked) {
            player.state = when {
                !player.isGrounded -> Player.State.JUMPING
                abs(player.velocityX) > 4f -> Player.State.MOVING
                else -> Player.State.IDLE
            }
        }
    }

    private fun moveTowards(current: Float, target: Float, maxDelta: Float): Float {
        if (current < target) {
            return min(current + maxDelta, target)
        }
        return max(current - maxDelta, target)
    }
}
