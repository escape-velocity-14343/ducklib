package com.escapevelocity.ducklib.core.math

fun umod(x: Byte, y: Byte) = ((x % y) + y) % y
fun umod(x: Short, y: Short) = ((x % y) + y) % y
fun umod(x: Int, y: Int) = ((x % y) + y) % y
fun umod(x: Long, y: Long) = ((x % y) + y) % y
fun umod(x: Float, y: Float) = ((x % y) + y) % y
fun umod(x: Double, y: Double) = ((x % y) + y) % y
