package com.escapevelocity.ducklib.core.geometry

data class Pose2(val position: Vector2, val heading: Radians) {
    constructor(x: Inches, y: Inches, heading: Radians): this(Vector2(x, y), heading)
    override fun toString() = "([${position.x}, ${position.y}] x $heading)"
}