package com.onimurasame.vania.simulation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class InputTraceIOTest {

    @Test
    fun `trace file roundtrip preserves frames`() {
        val trace = listOf(
            FrameInput(left = true),
            FrameInput(right = true, jumpJustPressed = true),
            FrameInput(attackJustPressed = true)
        )

        val file = File("build/tmp/test-trace.roundtrip.trace")
        InputTraceIO.write(file, trace)
        val loaded = InputTraceIO.read(file)

        assertEquals(trace, loaded)
    }

    @Test
    fun `loaded trace replay matches direct replay`() {
        val trace = GameSimulation.randomTrace(seed = 42, frames = 180)
        val file = File("build/tmp/test-trace.replay.trace")
        InputTraceIO.write(file, trace)
        val loaded = InputTraceIO.read(file)

        val directSim = GameSimulation()
        val loadedSim = GameSimulation()
        trace.forEach { directSim.step(it) }
        loaded.forEach { loadedSim.step(it) }

        assertEquals(directSim.snapshot(), loadedSim.snapshot())
    }
}
