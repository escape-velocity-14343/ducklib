package com.escapevelocity.ducklib.util

fun Any.b16Hash(): String = "${this::class.simpleName ?: "<object>"}@${(this.hashCode().toLong() and 0xffffffffL).toString(16)}"