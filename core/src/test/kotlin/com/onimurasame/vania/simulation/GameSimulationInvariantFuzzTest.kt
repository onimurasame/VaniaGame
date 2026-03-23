package com.onimurasame.vania.simulation

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GameSimulationInvariantFuzzTest {

    @Test
    fun `fuzzed traces maintain core invariants`() {
        repeat(200) { seed ->
            val sim = GameSimulation()
            val trace = GameSimulation.randomTrace(seed = seed, frames = 300)

            trace.forEachIndexed { frame, input ->
                val snap = sim.step(input)
                assertTrue(snap.playerHp in 0..100, "HP out of bounds at seed=$seed frame=$frame")
                assertTrue(snap.enemyHp in 0..60, "Enemy HP out of bounds at seed=$seed frame=$frame")
                assertTrue(snap.roomIndex in 0..2, "Invalid room index at seed=$seed frame=$frame")
                assertTrue(snap.playerX in 0f..772f, "Player X out of bounds at seed=$seed frame=$frame")
            }
        }
    }
}
