package com.escapevelocity.ducklib.core.geometry

data class Vector2(val x: Inches, val y: Inches) {
    enum class Axis {
        X,
        Y,
    }

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
    operator fun div(right: Vector2) = Vector2(x / right.x, y / right.y)
    operator fun div(right: Inches) = Vector2(x / right, y / right)
    infix fun dot(right: Vector2) = x * right.x + y * right.y

    operator fun get(index: Axis): Inches = when (index) {
        Axis.X -> x
        Axis.Y -> y
    }

    companion object Factory {
        val ZERO = Vector2(0.0.inches, 0.0.inches)
        fun fromAngle(angle: Radians, length: Inches = 1.0.inches) = Vector2(cos(angle) * length, sin(angle) * length)
    }
}