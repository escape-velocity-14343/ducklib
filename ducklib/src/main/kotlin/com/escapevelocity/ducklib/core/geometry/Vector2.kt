package com.escapevelocity.ducklib.core.geometry

/**
 * Data container that holds an X and Y position in [Inches] normally,
 * but sometimes it's just a scalar using an [Inches] wrapper.
 */
data class Vector2(val x: Inches, val y: Inches) {

    constructor(value: Inches) : this(value, value)

    override fun toString() = "($x, $y)"

    val angle
        get() = atan2(y, x)
    val length
        get() = hypot(x, y)
    val lengthSquared
        get() = (x * x + y * y).v
    val normalized
        get() = this / length
    val yx
        get() = Vector2(y, x)
    val xx
        get() = Vector2(x, x)
    val yy
        get() = Vector2(y, y)

    fun rotated(t: Radians) = Vector2(x * cos(t) - y * sin(t), y * cos(t) + x * sin(t))
    fun distanceTo(other: Vector2) = (other - this).length
    fun distanceSquaredTo(other: Vector2) = (other - this).lengthSquared
    fun angleTo(other: Vector2) = (other - this).angle
    fun setLength(length: Inches) = this.normalized * length
    fun limitLength(length: Inches) = if (this.length > length) setLength(length) else this

    operator fun plus(right: Vector2) = Vector2(x + right.x, y + right.y)
    operator fun minus(right: Vector2) = Vector2(x - right.x, y - right.y)
    operator fun times(right: Vector2) = Vector2(x * right.x, y * right.y)
    operator fun times(right: Inches) = Vector2(x * right, y * right)
    operator fun times(right: Double) = Vector2(x * right, y * right)
    operator fun div(right: Vector2) = Vector2(x / right.x, y / right.y)
    operator fun div(right: Inches) = Vector2(x / right, y / right)
    operator fun div(right: Double) = Vector2(x / right, y / right)
    infix fun dot(right: Vector2) = x * right.x + y * right.y

    fun flip(axis: Axis) = when (axis) {
        Axis.X -> Vector2(-x, +y)
        Axis.Y -> Vector2(+x, -y)
    }

    operator fun get(index: Axis): Inches = when (index) {
        Axis.X -> x
        Axis.Y -> y
    }

    companion object Factory {
        val ZERO = Vector2(0.0.inches)
        val ONE = Vector2(1.0.inches)
        val X = Vector2(1.0.inches, 0.0.inches)
        val Y = Vector2(0.0.inches, 1.0.inches)
        fun fromAngle(angle: Radians, length: Inches = 1.0.inches) = Vector2(cos(angle) * length, sin(angle) * length)
    }
}

enum class Axis {
    X,
    Y,
}