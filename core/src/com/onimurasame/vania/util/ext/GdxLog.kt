package com.onimurasame.vania.util.ext

import com.badlogic.gdx.utils.Logger

inline fun <reified T : Any> logger() = Logger(T::class.java.simpleName, Logger.DEBUG)