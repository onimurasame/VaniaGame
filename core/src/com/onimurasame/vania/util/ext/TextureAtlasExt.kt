package com.onimurasame.vania.util.ext

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion

inline operator fun TextureAtlas.invoke(regionName: String): TextureRegion? = findRegion(regionName)

inline operator fun TextureAtlas.get(regionName: String): TextureRegion? = findRegion(regionName)

fun TextureAtlas.findAllRegions(name: String): GdxArray<TextureAtlas.AtlasRegion> {
    val matched: GdxArray<TextureAtlas.AtlasRegion> = GdxArray()
    var i = 0
    val n = regions.size

    while (i < n) {
        val region = regions.get(i)
        if (region.name.contains(name)) matched.add(TextureAtlas.AtlasRegion(region))
        i++
    }

    return matched
}