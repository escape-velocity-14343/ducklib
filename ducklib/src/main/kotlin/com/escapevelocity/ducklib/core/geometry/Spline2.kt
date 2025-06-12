package com.escapevelocity.ducklib.core.geometry

import kotlin.math.pow
import kotlin.math.PI

// TODO: convert everything to subclasses bc i think that will work better or maybe don't? idk

const val ARCLENGTH_PRECISION = 100

fun cubicBezier(p1: Vector2, p2: Vector2, p3: Vector2, p4: Vector2) =
    { t: Double ->
        {
            val tm = 1.0 - t
            SplinePoint(
                p1 * tm.pow(3.0) + p2 * 3.0 * tm.pow(2.0) * t + p3 * 3.0 * tm * t.pow(2.0) + p4 * t.pow(
                    3.0
                ),
                3.0 * tm.pow(2.0) * (p2 - p1) + 6.0 * tm * t * (p3 - p2) + 3.0 * t.pow(2.0) * (p4 - p3),
                6.0 * tm * (p3 - 2.0 * p2 + p1) + 6.0 * t * (p4 - 2.0 * p3 + p2)
            )
        }
    }

fun cubicHermite(start: Vector2, startVelocity: Vector2, end: Vector2, endVelocity: Vector2) =
    cubicBezier(start, start - startVelocity / 3.0, end, end - endVelocity / 3.0)

fun lerp2(p1: Vector2, p2: Vector2) =
    { t: Double -> SplinePoint((1.0 - t) * p1 + t * p2, p2 - p1, Vector2.ZERO) }

class Spline2(val spline: (Double) -> SplinePoint) {

    val arclength by lazy {
        (0..<ARCLENGTH_PRECISION)
            .map { it.toDouble() / ARCLENGTH_PRECISION.toDouble() }
            .map { spline(it) }
            .fold(0.0.inches) { acc, x -> acc + x.velocity.length / ARCLENGTH_PRECISION.toDouble() }
    }

    operator fun invoke(t: Double) = spline(t)
    operator fun invoke(t: Inches) = spline((t / arclength).toDouble())

}

// i want position velocity and acceleration to be lazy bc you rlly shouldn't have to eval all 3 to get 1
// but rn idk how without making every spline a class which honestly wouldn't be so bad
data class SplinePoint(val position: Vector2, val velocity: Vector2, val acceleration: Vector2) {
    val curvature get() = (velocity cross acceleration) / velocity.lengthSquared.pow(3.0 / 2.0)

    /**
     * The normalized vector perpendicular to the tangent vector
     * (rotated 90 degrees counterclockwise), otherwise known as the normal vector.
     */
    val normal get() = velocity.rotated((PI / 2.0).radians).normalized

    val tangent get() = velocity.normalized

}