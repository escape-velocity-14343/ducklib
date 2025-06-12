package com.escapevelocity.ducklib.core.geometry

/**
 * Data container that holds a position and a heading.
 */
data class Pose2(val position: Vector2, val heading: Radians) {
    constructor(x: Inches, y: Inches, heading: Radians) : this(Vector2(x, y), heading)

    val xyh get() = Triple(position.x, position.y, heading)

    operator fun plus(other: Pose2) = Pose2(position + other.position, heading.rotated(other.heading))
    operator fun plus(other: Vector2) = Pose2(position + other, heading)
    operator fun plus(other: Radians) = Pose2(position, heading.rotated(other))

    operator fun minus(other: Pose2) = Pose2(position - other.position, heading.rotated(-other.heading))
    operator fun minus(other: Vector2) = Pose2(position - other, heading)
    operator fun minus(other: Radians) = Pose2(position, heading.rotated(-other))

    /**
     * Calculates the coordinates of this, in relation to the coordinates of [other].
     *
     * @param other the other [Pose2] to calculate the coordinates in relation to
     */
    fun relativeTo(other: Pose2) = Pose2((this - other.position).position.rotated(other.heading), (this - other.heading).heading)

    /**
     * Simultaneously calculates the positional and angular distance between this and [other].
     *
     * @param other The other [Pose2] to calculate the distance to
     */
    fun distanceTo(other: Pose2) = Pair(position.distanceTo(other.position), heading.angleTo(other.heading))

    /**
     * Calculates the positional distance from this to [other].
     *
     * Heading is ignored for this calculation.
     * If you want to calculate the angular distance as well, use the other `distanceTo`
     *
     * @param other The other [Vector2] to calculate the distance to
     */
    fun distanceTo(other: Vector2) = position.distanceTo(other)

    override fun toString() = "($position x $heading)"

    companion object {
        val ZERO = Pose2(Vector2.ZERO, Radians.ZERO)
    }
}