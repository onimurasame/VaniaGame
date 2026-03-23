package com.onimurasame.vania.simulation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class ReplayExpectationIOTest {

    @Test
    fun `expectation file roundtrip preserves values`() {
        val expectation = ReplayExpectation(
            roomIndex = 2,
            playerX = 133.5f,
            playerY = 241.25f,
            playerHp = 92,
            enemyHp = 20,
            enemyAlive = true,
            hasDoubleJump = true
        )
        val file = File("build/tmp/test-expect.roundtrip.expect")
        ReplayExpectationIO.write(file, expectation)

        val loaded = ReplayExpectationIO.read(file)
        assertEquals(expectation, loaded)
    }
}
