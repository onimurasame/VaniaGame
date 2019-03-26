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
        maxWidth = 4096
        maxHeight = 4096
    }

    TexturePacker.process(settings, "${AssetPacker.RAW_ASSETS_PATH}/player", "${AssetPacker.ASSETS_PATH}/player", "player")
    TexturePacker.process(settings, "${AssetPacker.RAW_ASSETS_PATH}/backgrounds/forest", "${AssetPacker.ASSETS_PATH}/backgrounds/forest", "forest_background")

}
