package com.onimurasame.vania.simulation

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GameSimulationCombatAndGateTest {

    @Test
    fun `attack input reduces enemy hp when in range`() {
        val sim = GameSimulation()
        sim.placePlayer(535f, 110f)

        val before = sim.snapshot().enemyHp
        sim.step(FrameInput(attackJustPressed = true))
        val after = sim.snapshot().enemyHp

        assertTrue(after < before, "Expected enemy HP to decrease after attack.")
    }

    @Test
    fun `room three remains gated until relic is acquired`() {
        val sim = GameSimulation()
        sim.setRoom(1)
        sim.placePlayer(770f, 110f)

        // Try to transition to room 3 before relic.
        sim.step(FrameInput(right = true))
        val gatedRoom = sim.snapshot().roomIndex

        // Acquire relic orb in room 2 and then transition.
        sim.placePlayer(620f, 310f)
        sim.step(FrameInput())
        sim.placePlayer(770f, 110f)
        sim.step(FrameInput(right = true))
        val unlockedRoom = sim.snapshot().roomIndex

        assertTrue(gatedRoom == 1, "Room should remain locked before relic pickup.")
        assertTrue(unlockedRoom == 2, "Room should unlock after relic pickup.")
    }
}
