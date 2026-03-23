package com.onimurasame.vania.simulation

import java.io.File

object InputTraceIO {
    private const val HEADER = "vania-trace-v1"

    fun write(traceFile: File, frames: List<FrameInput>) {
        traceFile.parentFile?.mkdirs()
        traceFile.bufferedWriter().use { out ->
            out.appendLine(HEADER)
            frames.forEach {
                out.appendLine(
                    "${bit(it.left)}," +
                        "${bit(it.right)}," +
                        "${bit(it.jumpJustPressed)}," +
                        "${bit(it.attackJustPressed)}"
                )
            }
        }
    }

    fun read(traceFile: File): List<FrameInput> {
        require(traceFile.exists()) { "Trace file not found: ${traceFile.absolutePath}" }
        val lines = traceFile.readLines()
        require(lines.isNotEmpty() && lines[0] == HEADER) { "Invalid trace file header." }
        return lines.drop(1).filter { it.isNotBlank() }.mapIndexed { index, line ->
            val parts = line.split(",")
            require(parts.size == 4) { "Invalid trace frame at line ${index + 2}" }
            FrameInput(
                left = bit(parts[0]),
                right = bit(parts[1]),
                jumpJustPressed = bit(parts[2]),
                attackJustPressed = bit(parts[3])
            )
        }
    }

    private fun bit(value: Boolean): Int = if (value) 1 else 0
    private fun bit(value: String): Boolean = value.trim() == "1"
}
