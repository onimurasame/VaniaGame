package com.onimurasame.vania.util.ext

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.Viewport

@JvmOverloads
fun Viewport.drawGrid(renderer: ShapeRenderer, cellSize : Int = 1 ) {
    val oldColor = renderer.color.cpy()
    val doubleWorldWidth = worldWidth * 2
    val doubleWorldHeight = worldHeight * 2

    apply()

    renderer.use {
        renderer.color = Color.WHITE

        // vertical lines

        var x = -doubleWorldWidth

        while(x < doubleWorldWidth) {
            renderer.line(x, -doubleWorldHeight, x, doubleWorldHeight)
            x += cellSize
        }

        var y = -doubleWorldHeight

        while(y < doubleWorldHeight) {
            renderer.line(-doubleWorldWidth, y, doubleWorldWidth, y)
            y += cellSize
        }

        renderer.color = Color.RED
        renderer.line(0f, -doubleWorldHeight, 0f, doubleWorldHeight)
        renderer.line(-doubleWorldWidth, 0f, doubleWorldWidth, 0f)

        renderer.color = Color.GREEN

        renderer.line(0f, worldHeight, worldWidth, worldHeight)
        renderer.line(worldWidth, 0f, worldWidth, worldHeight)

    }

    renderer.color = oldColor

}