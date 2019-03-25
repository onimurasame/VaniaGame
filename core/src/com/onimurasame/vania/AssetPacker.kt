package com.onimurasame.vania

import com.badlogic.gdx.tools.texturepacker.TexturePacker

object AssetPacker {

    const val DRAW_DEBUG_OUTLINE = false
    const val RAW_ASSETS_PATH = "core/assets-raw"
    const val ASSETS_PATH = "android/assets"

}

fun main(args: Array<String>) {
    val settings = TexturePacker.Settings().apply {
        debug = AssetPacker.DRAW_DEBUG_OUTLINE
        maxWidth = 2048
        maxHeight = 2048
    }

    TexturePacker.process(settings, "${AssetPacker.RAW_ASSETS_PATH}/player", "${AssetPacker.ASSETS_PATH}/player", "player")
}
