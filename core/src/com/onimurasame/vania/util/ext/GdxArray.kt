package com.onimurasame.vania.util.ext

typealias GdxArray<T> = com.badlogic.gdx.utils.Array<T>

fun <T> GdxArray<T>?.isEmpty() = this == null || this.size == 0

fun <T> GdxArray<T>?.isNotEmpty() = !isEmpty()