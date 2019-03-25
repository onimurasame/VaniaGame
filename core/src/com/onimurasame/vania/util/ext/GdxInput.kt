package com.onimurasame.vania.util.ext

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input

fun Int.isKeyPressed() = Gdx.input.isKeyPressed(this)
fun Int.asKeyString() = Input.Keys.toString(this)