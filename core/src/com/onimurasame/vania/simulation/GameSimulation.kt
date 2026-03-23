package com.onimurasame.vania.simulation

import kotlin.math.max
import kotlin.math.min
import kotlin.math.abs
import kotlin.random.Random

data class SimRect(var x: Float, var y: Float, var width: Float, var height: Float) {
    val left: Float get() = x
    val right: Float get() = x + width
    val bottom: Float get() = y
    val top: Float get() = y + height

    fun overlaps(other: SimRect): Boolean {
        return right > other.left && left < other.right && top > other.bottom && bottom < other.top
    }
}

data class FrameInput(
    val left: Boolean = false,
    val right: Boolean = false,
    val jumpJustPressed: Boolean = false,
    val attackJustPressed: Boolean = false
)

data class GameSnapshot(
    val roomIndex: Int,
    val playerX: Float,
    val playerY: Float,
    val velocityY: Float,
    val playerHp: Int,
    val enemyHp: Int,
    val enemyAlive: Boolean,
    val hasDoubleJump: Boolean,
    val onGround: Boolean
)

class GameSimulation {
    private data class Room(
        val platforms: List<SimRect>,
        val enemySpawn: SimRect?,
        val hasDoubleJumpOrb: Boolean
    )

    private val roomWidth = 800f
    private val player = SimRect(120f, 110f, 28f, 52f)
    private val ground = SimRect(0f, 80f, 800f, 30f)
    private var enemy = SimRect(560f, 110f, 30f, 50f)
    private var enemyAlive = true
    private var enemyDirection = 1f
    private var enemyPatrolLeft = 520f
    private var enemyPatrolRight = 700f
    private var roomIndex = 0

    private val rooms = listOf(
        Room(listOf(SimRect(350f, 220f, 170f, 20f)), SimRect(560f, 110f, 30f, 50f), false),
        Room(listOf(SimRect(140f, 190f, 120f, 20f), SimRect(500f, 260f, 170f, 20f)), SimRect(180f, 110f, 30f, 50f), true),
        Room(listOf(SimRect(100f, 280f, 120f, 20f), SimRect(320f, 330f, 120f, 20f), SimRect(540f, 390f, 120f, 20f)), SimRect(620f, 110f, 30f, 50f), false)
    )

    private var velocityY = 0f
    private var onGround = false
    private var facingRight = true
    private var jumpsUsed = 0
    private var hasDoubleJump = false
    private var attackCooldown = 0f
    private var hurtCooldown = 0f
    private var playerHp = 100
    private var enemyHp = 60
    private val enemyMaxHp = 60
    private val str = 8

    fun step(input: FrameInput, delta: Float = 1f / 60f): GameSnapshot {
        val moveSpeed = 180f
        val jumpVelocity = 510f
        val gravity = -1450f

        attackCooldown = max(0f, attackCooldown - delta)
        hurtCooldown = max(0f, hurtCooldown - delta)

        var velocityX = 0f
        if (input.left) {
            velocityX = -moveSpeed
            facingRight = false
        }
        if (input.right) {
            velocityX = moveSpeed
            facingRight = true
        }

        val attackRange = if (facingRight) {
            SimRect(player.x + player.width - 4f, player.y + 6f, 48f, player.height - 12f)
        } else {
            SimRect(player.x - 44f, player.y + 6f, 48f, player.height - 12f)
        }
        if (input.attackJustPressed && enemyAlive && attackCooldown <= 0f && attackRange.overlaps(enemy)) {
            enemyHp = max(0, enemyHp - (10 + str))
            enemyAlive = enemyHp > 0
            attackCooldown = 0.25f
        }

        if (input.jumpJustPressed) {
            val canJump = onGround || (hasDoubleJump && jumpsUsed < 2)
            if (canJump) {
                velocityY = jumpVelocity
                onGround = false
                jumpsUsed++
            }
        }

        val previousX = player.x
        val previousY = player.y
        velocityY += gravity * delta

        player.x += velocityX * delta
        resolveHorizontalCollisions(previousX, velocityX)

        player.y += velocityY * delta

        if (player.overlaps(ground) && velocityY <= 0f && player.y + 6f >= ground.top) {
            player.y = ground.top
            velocityY = 0f
            onGround = true
        } else {
            onGround = false
        }

        if (!onGround) {
            val previousBottom = previousY
            val previousTop = previousY + player.height
            val nextBottom = player.y
            val nextTop = player.y + player.height
            for (platform in rooms[roomIndex].platforms) {
                val horizontalOverlap = player.right > platform.left + 2f && player.left < platform.right - 2f
                if (!horizontalOverlap) continue

                val crossedTopWhileFalling = velocityY <= 0f && previousBottom >= platform.top && nextBottom <= platform.top
                if (crossedTopWhileFalling) {
                    player.y = platform.top
                    velocityY = 0f
                    onGround = true
                    break
                }

                val crossedBottomWhileRising = velocityY > 0f && previousTop <= platform.bottom && nextTop >= platform.bottom
                if (crossedBottomWhileRising) {
                    player.y = platform.bottom - player.height
                    velocityY = 0f
                    break
                }
            }
        }

        if (onGround) jumpsUsed = 0

        player.x = min(max(player.x, 0f), roomWidth - player.width)
        if (player.y < -50f) {
            player.y = ground.top
            velocityY = 0f
            jumpsUsed = 0
            playerHp = 100
            enemyHp = enemyMaxHp
            enemyAlive = true
        }

        handleRoomTransitions()
        handleOrbPickup()
        updateEnemy(delta)
        resolveContactDamage()

        return snapshot()
    }

    fun snapshot(): GameSnapshot = GameSnapshot(
        roomIndex = roomIndex,
        playerX = player.x,
        playerY = player.y,
        velocityY = velocityY,
        playerHp = playerHp,
        enemyHp = enemyHp,
        enemyAlive = enemyAlive,
        hasDoubleJump = hasDoubleJump,
        onGround = onGround
    )

    fun canPenetrateSolid(): Boolean {
        if (player.overlaps(ground)) {
            val deepInsideGround = player.bottom < ground.top - 6.0f
            if (deepInsideGround) return true
        }
        return rooms[roomIndex].platforms.any { platform ->
            val overlapping = player.overlaps(platform)
            if (!overlapping) return@any false

            val deepVerticalOverlap = player.bottom < platform.top - 6.0f && player.top > platform.bottom + 6.0f
            val touchingTop = abs(player.bottom - platform.top) <= 2.0f
            val touchingBottom = abs(player.top - platform.bottom) <= 2.0f
            deepVerticalOverlap && !touchingTop && !touchingBottom
        }
    }

    fun placePlayer(x: Float, y: Float) {
        player.x = x
        player.y = y
    }

    fun setRoom(index: Int) {
        roomIndex = index.coerceIn(0, rooms.lastIndex)
        configureRoom()
    }

    private fun handleRoomTransitions() {
        if (player.right >= roomWidth - 2f && roomIndex < rooms.lastIndex) {
            val nextLocked = roomIndex == 1 && !hasDoubleJump
            if (!nextLocked) {
                roomIndex++
                player.x = 6f
                configureRoom()
            } else {
                player.x = roomWidth - player.width - 4f
            }
        } else if (player.left <= 2f && roomIndex > 0) {
            roomIndex--
            player.x = roomWidth - player.width - 6f
            configureRoom()
        }
    }

    private fun handleOrbPickup() {
        val room = rooms[roomIndex]
        if (room.hasDoubleJumpOrb && !hasDoubleJump) {
            val orbRect = SimRect(620f, 310f, 24f, 24f)
            if (player.overlaps(orbRect)) {
                hasDoubleJump = true
            }
        }
    }

    private fun updateEnemy(delta: Float) {
        if (!enemyAlive) return
        enemy.x += enemyDirection * 70f * delta
        if (enemy.x <= enemyPatrolLeft) {
            enemy.x = enemyPatrolLeft
            enemyDirection = 1f
        } else if (enemy.x >= enemyPatrolRight) {
            enemy.x = enemyPatrolRight
            enemyDirection = -1f
        }
    }

    private fun resolveContactDamage() {
        if (enemyAlive && player.overlaps(enemy) && hurtCooldown <= 0f) {
            playerHp = max(0, playerHp - 8)
            hurtCooldown = 0.9f
        }
    }

    private fun resolveHorizontalCollisions(previousX: Float, velocityX: Float) {
        if (velocityX == 0f) return
        val verticalInset = 4f
        for (platform in rooms[roomIndex].platforms) {
            val verticalOverlap = player.top > platform.bottom + verticalInset && player.bottom < platform.top - verticalInset
            if (!verticalOverlap || !player.overlaps(platform)) continue

            if (velocityX > 0f && previousX + player.width <= platform.left) {
                player.x = platform.left - player.width
            } else if (velocityX < 0f && previousX >= platform.right) {
                player.x = platform.right
            }
        }
    }

    private fun configureRoom() {
        val spawn = rooms[roomIndex].enemySpawn
        if (spawn != null) {
            enemy = SimRect(spawn.x, spawn.y, spawn.width, spawn.height)
            enemyPatrolLeft = enemy.x - 80f
            enemyPatrolRight = enemy.x + 120f
            enemyAlive = true
            enemyHp = enemyMaxHp
            enemyDirection = 1f
        } else {
            enemyAlive = false
            enemyHp = 0
        }
    }

    companion object {
        fun randomTrace(seed: Int, frames: Int): List<FrameInput> {
            val random = Random(seed)
            return (0 until frames).map {
                FrameInput(
                    left = random.nextBoolean() && !random.nextBoolean(),
                    right = random.nextBoolean() && !random.nextBoolean(),
                    jumpJustPressed = random.nextInt(8) == 0,
                    attackJustPressed = random.nextInt(10) == 0
                )
            }
        }
    }
}
