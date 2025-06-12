package com.escapevelocity.ducklib.ftc.waddle

import com.escapevelocity.ducklib.core.geometry.Inches
import com.escapevelocity.ducklib.core.geometry.Vector2
import com.escapevelocity.ducklib.core.geometry.Spline2
import com.escapevelocity.ducklib.core.geometry.SplinePoint
import com.escapevelocity.ducklib.core.geometry.floor
import com.escapevelocity.ducklib.core.geometry.inches
import com.qualcomm.robotcore.util.ElapsedTime
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.*

// TODO: kdocs
// TODO: code cleanup
// TODO: scope cleanup
// TODO: make the profile generation part work
// TODO: check for spline continuity
// TODO: trajectory builder

fun Vector2.lerpWith(other: Vector2): (Double) -> Vector2 =
    { t: Double -> this * t + other * (1 - t) }

fun secondsToGoDistance(v0: Inches, a: Inches, d: Inches): Double =
    ((-v0 + sqrt((v0.toDouble().pow(2).inches + a * d * 2.0).toDouble())) / a).toDouble()

/**
 * Standard Waddle Trajectory class. Represents a single Unit of movement.
 * @param deltaInches The number of inches between each computed motion profile state.
 * Decreasing this number will increase follower performance at the cost of generation time.
 */
class Trajectory(private vararg val splines: Spline2, val deltaInches: Inches = 0.5.inches) {

    /**
     * Used for tracking system time for automatic motion profile fetching.
     */
    private val timeElapsed: ElapsedTime = ElapsedTime()

    // TODO: make this exist
    val profiles: List<DiscreteMotionProfileState> by lazy {
        mutableListOf(
            DiscreteMotionProfileState.ZERO
        )
    }

    // TODO: make sure i pass in deltaInches here
    val profile: DiscreteMotionProfileState
        get() {

            val currentSeconds = timeElapsed.seconds()
            val currentInches = secondsToInches(currentSeconds)
            return getInterpolatedProfile(currentInches)

        }

    // funny alias for funsies
    operator fun invoke() = profile

    val totalArclength: Inches = splines.fold(0.0.inches) { acc, x -> acc + x.arclength }
    val totalSeconds: Double = profiles.map { it.seconds }.fold(0.0) { acc, x -> acc + x }

    // TODO: i should rename this tbh
    /**
     * Initializes the trajectory for following. This signals that the robot has started following the trajectory at this instance.
     */
    fun init() = timeElapsed.reset()

    /**
     * Converts a number of seconds spent following the trajectory into the estimated number
     * of inches along the trajectory, based on the motion profiles.
     */
    fun secondsToInches(seconds: Double): Inches {
        var remainingSeconds = seconds
        if (seconds >= totalSeconds) {
            return totalArclength
        }

        // i love functional programming
        for ((i, s) in profiles.map { it.seconds }.withIndex()) {

            // cursed use of when
            when {
                remainingSeconds < s -> return ((deltaInches * i)
                        + profiles[i].velocity.length * remainingSeconds
                        + profiles[i].acceleration.length * remainingSeconds.pow(2) / 2.0)

                else -> remainingSeconds -= s
            }

        }

        throw Error(
            "what the fuck this state should be impossible to reach, if you're a waddle developer " +
                    "double check your code or if you're a waddle user message the waddle maintainers"
        )
            .fillInStackTrace()
    }

    fun getInterpolatedProfile(inches: Inches): DiscreteMotionProfileState {
        val index = floor(inches / deltaInches).toDouble().toInt()
            .coerceIn(0, profiles.size - 1)
        val profile = profiles[index]

        // inches not accounted for by a discrete profile
        val nondiscreteInches = inches - deltaInches * index

        // time not accounted for by a discrete profile
        val nondiscreteTime = secondsToGoDistance(
            profile.velocity.length,
            profile.acceleration.length,
            nondiscreteInches,
        )

        // resulting "interpolated" velocity, based on acceleration that should've happened
        val newVel = (profile.velocity +
                profile.acceleration * nondiscreteTime)

        return DiscreteMotionProfileState(
            splines(inches).position,
            newVel,
            profile.acceleration
        )
    }

    /**
     * @return The `SplinePoint` corresponding to the point `d` inches along the sequence of splines.
     */
    operator fun Array<out Spline2>.invoke(d: Inches): SplinePoint {
        val data = this.foldIndexed(Pair<Int, Inches>(0, d)) {i, remainingDist, currSpline ->

            if (remainingDist.second > currSpline.arclength) {
                Pair(i, remainingDist.second - currSpline.arclength)
            } else {
                remainingDist
            }

        }

        return this.elementAt(data.first)(data.second)
    }

    /**
     * @return The `SplinePoint` corresponding to the point `t` value along the sequence of splines.
     */
    operator fun Array<out Spline2>.invoke(t: Double): SplinePoint {
        return this.elementAt((t / 1.0).toInt().coerceIn(0, this.size - 1))(t - floor(t))
    }

}


data class DiscreteMotionProfileState(
    val position: Vector2,
    val velocity: Vector2,
    val acceleration: Vector2,
    val deltaInches: Inches = 0.5.inches,
) {


    /**
     * Denotes the number of seconds until the next motion profile state will be reached, starting from this one.
     */
    val seconds: Double by lazy {
        secondsToGoDistance(
            velocity.length,
            acceleration.length,
            deltaInches,
        )
    }

    companion object {
    val ZERO = DiscreteMotionProfileState(Vector2.ZERO, Vector2.ZERO, Vector2.ZERO)

        // t = (-v_0 + sqrt(v_0^2 + 2*a(t)*d(t))) / a(t)

    }

}

val asdf = Trajectory(Spline2({ SplinePoint(Vector2.ZERO, Vector2.ZERO, Vector2.ZERO) })).profile
