package com.onimurasame.vania.util.ext

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle

fun String.toInternalFile(): FileHandle = Gdx.files.internal(this)