package com.escapevelocity.ducklib.util

fun Any.b16Hash(): String = (this.hashCode().toLong() and 0xffffffffL).toString(16)