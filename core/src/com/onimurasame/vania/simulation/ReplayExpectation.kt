package com.onimurasame.vania.simulation

import java.io.File
import kotlin.math.abs

data class ReplayExpectation(
    val roomIndex: Int,
    val playerX: Float,
    val playerY: Float,
    val playerHp: Int,
    val enemyHp: Int,
    val enemyAlive: Boolean,
    val hasDoubleJump: Boolean
) {
    fun assertMatches(snapshot: GameSnapshot, positionTolerance: Float = 0.01f) {
        require(snapshot.roomIndex == roomIndex) { "Expected roomIndex=$roomIndex, got ${snapshot.roomIndex}" }
        require(abs(snapshot.playerX - playerX) <= positionTolerance) { "Expected playerX=$playerX, got ${snapshot.playerX}" }
        require(abs(snapshot.playerY - playerY) <= positionTolerance) { "Expected playerY=$playerY, got ${snapshot.playerY}" }
        require(snapshot.playerHp == playerHp) { "Expected playerHp=$playerHp, got ${snapshot.playerHp}" }
        require(snapshot.enemyHp == enemyHp) { "Expected enemyHp=$enemyHp, got ${snapshot.enemyHp}" }
        require(snapshot.enemyAlive == enemyAlive) { "Expected enemyAlive=$enemyAlive, got ${snapshot.enemyAlive}" }
        require(snapshot.hasDoubleJump == hasDoubleJump) { "Expected hasDoubleJump=$hasDoubleJump, got ${snapshot.hasDoubleJump}" }
    }
}

object ReplayExpectationIO {
    private const val HEADER = "vania-expectation-v1"

    fun write(file: File, expectation: ReplayExpectation) {
        file.parentFile?.mkdirs()
        file.bufferedWriter().use { out ->
            out.appendLine(HEADER)
            out.appendLine("roomIndex=${expectation.roomIndex}")
            out.appendLine("playerX=${expectation.playerX}")
            out.appendLine("playerY=${expectation.playerY}")
            out.appendLine("playerHp=${expectation.playerHp}")
            out.appendLine("enemyHp=${expectation.enemyHp}")
            out.appendLine("enemyAlive=${expectation.enemyAlive}")
            out.appendLine("hasDoubleJump=${expectation.hasDoubleJump}")
        }
    }

    fun read(file: File): ReplayExpectation {
        require(file.exists()) { "Expectation file not found: ${file.absolutePath}" }
        val lines = file.readLines().filter { it.isNotBlank() }
        require(lines.firstOrNull() == HEADER) { "Invalid expectation file header." }
        val map = lines.drop(1).associate {
            val parts = it.split("=", limit = 2)
            require(parts.size == 2) { "Invalid expectation line: $it" }
            parts[0].trim() to parts[1].trim()
        }
        return ReplayExpectation(
            roomIndex = map.getValue("roomIndex").toInt(),
            playerX = map.getValue("playerX").toFloat(),
            playerY = map.getValue("playerY").toFloat(),
            playerHp = map.getValue("playerHp").toInt(),
            enemyHp = map.getValue("enemyHp").toInt(),
            enemyAlive = map.getValue("enemyAlive").toBooleanStrict(),
            hasDoubleJump = map.getValue("hasDoubleJump").toBooleanStrict()
        )
    }
}
