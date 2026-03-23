package com.onimurasame.vania.simulation

import java.io.File

object TraceReplayCli {
    @JvmStatic
    fun main(args: Array<String>) {
        val pathArg = args.firstOrNull()
            ?: throw IllegalArgumentException("Usage: TraceReplayCli <trace-file-path> [expectation-file-path]")
        val expectationArg = args.getOrNull(1)
        val traceFile = File(pathArg)
        val frames = InputTraceIO.read(traceFile)

        val sim = GameSimulation()
        var firstInvalidPenetrationFrame = -1
        frames.forEachIndexed { index, frame ->
            sim.step(frame)
            if (firstInvalidPenetrationFrame < 0 && sim.canPenetrateSolid()) {
                firstInvalidPenetrationFrame = index
            }
        }
        val snap = sim.snapshot()
        val ok = firstInvalidPenetrationFrame < 0

        if (expectationArg != null) {
            val expectation = ReplayExpectationIO.read(File(expectationArg))
            expectation.assertMatches(snap)
        }

        println("Replay frames: ${frames.size}")
        println("Final room: ${snap.roomIndex}")
        println("Final player: (${snap.playerX}, ${snap.playerY}) hp=${snap.playerHp}")
        println("Final enemy hp: ${snap.enemyHp} alive=${snap.enemyAlive}")
        println("Has relic: ${snap.hasDoubleJump}")
        if (expectationArg != null) {
            println("Golden expectation: PASS")
        }
        println("Collision invariant: ${if (ok) "PASS" else "FAIL at frame $firstInvalidPenetrationFrame"}")

        if (!ok) {
            throw IllegalStateException("Replay invariant failure at frame $firstInvalidPenetrationFrame")
        }
    }
}
