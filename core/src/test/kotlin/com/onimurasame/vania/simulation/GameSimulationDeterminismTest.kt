package com.onimurasame.vania.simulation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GameSimulationDeterminismTest {

    @Test
    fun `same input trace produces identical snapshots`() {
        val trace = GameSimulation.randomTrace(seed = 1337, frames = 400)

        val simA = GameSimulation()
        val simB = GameSimulation()

        trace.forEach { frame ->
            val a = simA.step(frame)
            val b = simB.step(frame)
            assertEquals(a, b, "Simulation diverged under identical replay input.")
        }
    }
}
