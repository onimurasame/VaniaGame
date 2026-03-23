package com.onimurasame.vania.simulation

import java.io.File

object TraceExpectationGeneratorCli {
    @JvmStatic
    fun main(args: Array<String>) {
        val tracePath = args.getOrNull(0)
            ?: throw IllegalArgumentException("Usage: TraceExpectationGeneratorCli <trace-file-path> <output-expectation-path>")
        val outputPath = args.getOrNull(1)
            ?: throw IllegalArgumentException("Usage: TraceExpectationGeneratorCli <trace-file-path> <output-expectation-path>")

        val frames = InputTraceIO.read(File(tracePath))
        val sim = GameSimulation()
        frames.forEach { sim.step(it) }
        val snap = sim.snapshot()
        val expectation = ReplayExpectation(
            roomIndex = snap.roomIndex,
            playerX = snap.playerX,
            playerY = snap.playerY,
            playerHp = snap.playerHp,
            enemyHp = snap.enemyHp,
            enemyAlive = snap.enemyAlive,
            hasDoubleJump = snap.hasDoubleJump
        )
        ReplayExpectationIO.write(File(outputPath), expectation)
        println("Golden expectation written: $outputPath")
    }
}
