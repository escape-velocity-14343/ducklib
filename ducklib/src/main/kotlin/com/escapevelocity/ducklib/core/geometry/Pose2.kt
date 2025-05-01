package com.escapevelocity.ducklib.core.geometry

data class Pose2(val position: Vector2, val heading: Radians) {
    constructor(x: Inches, y: Inches, heading: Radians) : this(Vector2(x, y), heading)

    operator fun plus(other: Pose2) = Pose2(position + other.position, heading.rotated(other.heading))
    operator fun minus(other: Pose2) = Pose2(position - other.position, heading.rotated(-other.heading))

    fun distance(other: Pose2) = Pair(position.distanceTo(other.position), heading.distanceTo(other.heading))

    override fun toString() = "([${position.x}, ${position.y}] x $heading)"
}