package com.escapevelocity.ducklib.core.util

fun Any.b16Hash(): String = (this.hashCode().toLong() and 0xffffffffL).toString(16)