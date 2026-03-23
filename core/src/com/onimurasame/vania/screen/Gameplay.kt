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
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.viewport.FitViewport
import com.onimurasame.vania.VaniaGame
import com.onimurasame.vania.configuration.GameConfig
import com.onimurasame.vania.simulation.FrameInput
import com.onimurasame.vania.simulation.InputTraceIO
import java.io.File

class Gameplay(game: VaniaGame) : ScreenAdapter() {
    private data class Room(
        val id: Int,
        val color: Color,
        val platforms: List<Rectangle>,
        val enemySpawn: Rectangle?,
        val hasDoubleJumpOrb: Boolean = false
    )

    private val appGame = game
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(GameConfig.WIDTH.toFloat(), GameConfig.HEIGHT.toFloat(), camera)
    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val font = BitmapFont()

    private val roomWidth = 800f
    private val roomHeight = 600f
    private val player = Rectangle(120f, 140f, 28f, 52f)
    private val ground = Rectangle(0f, 80f, 800f, 30f)
    private var enemy = Rectangle(560f, 110f, 30f, 50f)
    private var enemyAlive = true
    private var enemyDirection = 1f
    private var enemyPatrolLeft = 520f
    private var enemyPatrolRight = 700f

    private val rooms = listOf(
        Room(
            id = 0,
            color = Color(0.16f, 0.16f, 0.22f, 1f),
            platforms = listOf(Rectangle(350f, 220f, 170f, 20f)),
            enemySpawn = Rectangle(560f, 110f, 30f, 50f),
            hasDoubleJumpOrb = false
        ),
        Room(
            id = 1,
            color = Color(0.14f, 0.18f, 0.22f, 1f),
            platforms = listOf(Rectangle(140f, 190f, 120f, 20f), Rectangle(500f, 260f, 170f, 20f)),
            enemySpawn = Rectangle(180f, 110f, 30f, 50f),
            hasDoubleJumpOrb = true
        ),
        Room(
            id = 2,
            color = Color(0.18f, 0.14f, 0.22f, 1f),
            platforms = listOf(Rectangle(100f, 280f, 120f, 20f), Rectangle(320f, 330f, 120f, 20f), Rectangle(540f, 390f, 120f, 20f)),
            enemySpawn = Rectangle(620f, 110f, 30f, 50f),
            hasDoubleJumpOrb = false
        )
    )
    private var currentRoomIndex = 0

    private var velocityX = 0f
    private var velocityY = 0f
    private var onGround = false
    private var facingRight = true
    private var jumpsUsed = 0
    private var hasDoubleJump = false
    private var attackCooldown = 0f
    private var hurtCooldown = 0f

    private var playerHp = 100
    private var playerMaxHp = 100
    private var enemyHp = 60
    private var enemyMaxHp = 60

    private var statusMenuOpen = false
    private var recordingTrace = false
    private val recordedFrames = mutableListOf<FrameInput>()

    private val level = 1
    private val str = 8
    private val def = 6
    private val agi = 5
    private val equippedWeapon = "Rusty Sword (+2 STR)"
    private val equippedArmor = "Cloth Tunic (+1 DEF)"
    private val inventory = mutableListOf(
        "Rusty Sword (Weapon)",
        "Short Dagger (Weapon)",
        "Cloth Tunic (Armor)",
        "Leather Vest (Armor)"
    )

    override fun show() {
        camera.setToOrtho(false, GameConfig.WIDTH.toFloat(), GameConfig.HEIGHT.toFloat())
        font.color = Color.WHITE
        font.data.setScale(1.0f)
    }

    override fun render(delta: Float) {
        handleGlobalInput()
        val frameInput = readFrameInput()
        handleTraceHotkeys()

        if (!statusMenuOpen) {
            if (recordingTrace) {
                recordedFrames.add(frameInput)
            }
            updatePhysics(delta, frameInput)
        }

        Gdx.gl.glClearColor(0.10f, 0.10f, 0.12f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        viewport.apply()
        camera.update()

        drawWorld()
        drawHud()

        if (statusMenuOpen) {
            drawStatusMenu()
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

    private fun handleGlobalInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (statusMenuOpen) {
                statusMenuOpen = false
            } else {
                appGame.screen = MainMenuScreen(appGame)
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.I) || Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            statusMenuOpen = !statusMenuOpen
        }
    }

    private fun readFrameInput(): FrameInput {
        return FrameInput(
            left = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT),
            right = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT),
            jumpJustPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.W),
            attackJustPressed = Gdx.input.isKeyJustPressed(Input.Keys.J)
        )
    }

    private fun handleTraceHotkeys() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F9)) {
            recordingTrace = !recordingTrace
            if (recordingTrace) {
                recordedFrames.clear()
            } else if (recordedFrames.isNotEmpty()) {
                val tracePath = File("build/traces/latest.trace")
                InputTraceIO.write(tracePath, recordedFrames)
            }
        }
    }

    private fun updatePhysics(delta: Float, input: FrameInput) {
        val moveSpeed = 180f
        val jumpVelocity = 510f
        val gravity = -1450f

        attackCooldown = maxOf(0f, attackCooldown - delta)
        hurtCooldown = maxOf(0f, hurtCooldown - delta)
        velocityX = 0f
        if (input.left) {
            velocityX = -moveSpeed
            facingRight = false
        }
        if (input.right) {
            velocityX = moveSpeed
            facingRight = true
        }

        val attackRange = if (facingRight) {
            Rectangle(player.x + player.width - 4f, player.y + 6f, 48f, player.height - 12f)
        } else {
            Rectangle(player.x - 44f, player.y + 6f, 48f, player.height - 12f)
        }
        if (input.attackJustPressed && enemyAlive && attackCooldown <= 0f && attackRange.overlaps(enemy)) {
            enemyHp = maxOf(0, enemyHp - (10 + str))
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

        if (player.overlaps(ground) && velocityY <= 0f && player.y + 6f >= ground.y + ground.height) {
            player.y = ground.y + ground.height
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
            for (platform in rooms[currentRoomIndex].platforms) {
                val horizontalOverlap = player.x + player.width > platform.x + 2f && player.x < platform.x + platform.width - 2f
                if (!horizontalOverlap) continue

                val platformTop = platform.y + platform.height
                val platformBottom = platform.y

                val crossedTopWhileFalling = velocityY <= 0f && previousBottom >= platformTop && nextBottom <= platformTop
                if (crossedTopWhileFalling) {
                    player.y = platform.y + platform.height
                    velocityY = 0f
                    onGround = true
                    break
                }

                val crossedBottomWhileRising = velocityY > 0f && previousTop <= platformBottom && nextTop >= platformBottom
                if (crossedBottomWhileRising) {
                    player.y = platformBottom - player.height
                    velocityY = 0f
                    break
                }
            }
        }

        if (onGround) {
            jumpsUsed = 0
        }

        if (player.x < 0f) player.x = 0f
        if (player.x + player.width > roomWidth) player.x = roomWidth - player.width
        if (player.y < -50f) {
            player.y = ground.y + ground.height
            velocityY = 0f
            jumpsUsed = 0
            playerHp = playerMaxHp
            enemyHp = enemyMaxHp
            enemyAlive = true
        }

        handleRoomTransitions()
        updateRoomPickups()
        updateEnemy(delta)
        resolveCombatContact()
    }

    private fun handleRoomTransitions() {
        if (player.x + player.width >= roomWidth - 2f && currentRoomIndex < rooms.lastIndex) {
            val nextLocked = currentRoomIndex == 1 && !hasDoubleJump
            if (!nextLocked) {
                currentRoomIndex++
                player.x = 6f
                configureRoomState()
            } else {
                player.x = roomWidth - player.width - 4f
            }
        } else if (player.x <= 2f && currentRoomIndex > 0) {
            currentRoomIndex--
            player.x = roomWidth - player.width - 6f
            configureRoomState()
        }
    }

    private fun updateRoomPickups() {
        val room = rooms[currentRoomIndex]
        if (room.hasDoubleJumpOrb && !hasDoubleJump) {
            val orbRect = Rectangle(620f, 310f, 24f, 24f)
            if (player.overlaps(orbRect)) {
                hasDoubleJump = true
                inventory.add("Soul Orb (Double Jump Relic)")
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

    private fun resolveCombatContact() {
        if (enemyAlive && player.overlaps(enemy) && hurtCooldown <= 0f) {
            playerHp = maxOf(0, playerHp - 8)
            hurtCooldown = 0.9f
            if (playerHp <= 0) {
                playerHp = playerMaxHp
                currentRoomIndex = 0
                player.x = 120f
                player.y = ground.y + ground.height
                configureRoomState()
            }
        }
    }

    private fun configureRoomState() {
        val room = rooms[currentRoomIndex]
        if (room.enemySpawn != null) {
            enemy = Rectangle(room.enemySpawn)
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

    private fun resolveHorizontalCollisions(previousX: Float, velocityX: Float) {
        if (velocityX == 0f) return
        val verticalInset = 4f
        for (platform in rooms[currentRoomIndex].platforms) {
            val verticalOverlap = player.y + player.height > platform.y + verticalInset &&
                player.y < platform.y + platform.height - verticalInset
            if (!verticalOverlap || !player.overlaps(platform)) continue

            if (velocityX > 0f && previousX + player.width <= platform.x) {
                player.x = platform.x - player.width
            } else if (velocityX < 0f && previousX >= platform.x + platform.width) {
                player.x = platform.x + platform.width
            }
        }
    }

    private fun drawWorld() {
        val room = rooms[currentRoomIndex]
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        shapeRenderer.color = room.color
        shapeRenderer.rect(0f, 0f, roomWidth, roomHeight)

        shapeRenderer.color = Color(0.22f, 0.22f, 0.30f, 1f)
        shapeRenderer.rect(ground.x, ground.y, ground.width, ground.height)
        for (platform in room.platforms) {
            shapeRenderer.rect(platform.x, platform.y, platform.width, platform.height)
        }

        shapeRenderer.color = if (hurtCooldown > 0f) Color(0.95f, 0.25f, 0.25f, 1f) else Color(0.90f, 0.45f, 0.20f, 1f)
        shapeRenderer.rect(player.x, player.y, player.width, player.height)

        if (enemyAlive) {
            shapeRenderer.color = Color(0.70f, 0.20f, 0.22f, 1f)
            shapeRenderer.rect(enemy.x, enemy.y, enemy.width, enemy.height)
        }

        if (room.hasDoubleJumpOrb && !hasDoubleJump) {
            shapeRenderer.color = Color(0.20f, 0.75f, 0.95f, 1f)
            shapeRenderer.rect(620f, 310f, 24f, 24f)
        }

        if (currentRoomIndex == 1 && !hasDoubleJump) {
            shapeRenderer.color = Color(0.45f, 0.12f, 0.12f, 1f)
            shapeRenderer.rect(760f, 110f, 30f, 140f)
        }

        shapeRenderer.end()
    }

    private fun drawHud() {
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        drawHealthBar(20f, 550f, 230f, 18f, playerHp, playerMaxHp, Color(0.75f, 0.12f, 0.12f, 1f))
        drawHealthBar(550f, 550f, 230f, 18f, enemyHp, enemyMaxHp, Color(0.75f, 0.12f, 0.12f, 1f))

        shapeRenderer.end()

        batch.projectionMatrix = camera.combined
        batch.begin()
        font.draw(batch, "PLAYER", 20f, 576f)
        font.draw(batch, "ENEMY", 550f, 576f)
        font.draw(batch, "Room ${currentRoomIndex + 1}/3", 360f, 576f)
        font.draw(batch, "Arrows/A,D Move  Space Jump  J Attack  I/TAB Status  ESC Menu", 20f, 35f)
        font.draw(batch, "F9 Trace: ${if (recordingTrace) "REC (${recordedFrames.size})" else "OFF"}", 20f, 55f)
        batch.end()
    }

    private fun drawHealthBar(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        current: Int,
        max: Int,
        fillColor: Color
    ) {
        val ratio = if (max <= 0) 0f else current.toFloat() / max.toFloat()
        shapeRenderer.color = Color(0.10f, 0.10f, 0.10f, 1f)
        shapeRenderer.rect(x - 2f, y - 2f, width + 4f, height + 4f)
        shapeRenderer.color = Color(0.20f, 0.20f, 0.20f, 1f)
        shapeRenderer.rect(x, y, width, height)
        shapeRenderer.color = fillColor
        shapeRenderer.rect(x, y, width * ratio, height)
    }

    private fun drawStatusMenu() {
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0f, 0f, 0f, 0.75f)
        shapeRenderer.rect(90f, 70f, 620f, 460f)
        shapeRenderer.end()

        batch.projectionMatrix = camera.combined
        batch.begin()
        font.draw(batch, "STATUS / INVENTORY", 120f, 500f)
        font.draw(batch, "Level: $level", 120f, 460f)
        font.draw(batch, "STR: $str   DEF: $def   AGI: $agi", 120f, 430f)
        font.draw(batch, "Relic: ${if (hasDoubleJump) "Soul Orb (Double Jump)" else "Not Acquired"}", 120f, 400f)
        font.draw(batch, "Weapon: $equippedWeapon", 120f, 390f)
        font.draw(batch, "Armor:  $equippedArmor", 120f, 360f)
        font.draw(batch, "Inventory:", 120f, 320f)

        var y = 290f
        for (item in inventory) {
            font.draw(batch, "- $item", 140f, y)
            y -= 28f
        }

        font.draw(batch, "Press I / TAB / ESC to close", 120f, 110f)
        batch.end()
    }
}
