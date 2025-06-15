package com.escapevelocity.ducklib.core.util

import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.DurationUnit

/**
 * A wrapper around a [Long] for measuring time.
 */
@JvmInline
value class ElapsedTime internal constructor(val startNs: Long) : Comparable<Duration> {
    inline val elapsed get() = (System.nanoTime() - startNs).nanoseconds
    operator fun plus(duration: Duration) = ElapsedTime(startNs + duration.inWholeNanoseconds)
    override fun compareTo(other: Duration) = other.inWholeNanoseconds.compareTo(startNs)
}

fun startTimer() = ElapsedTime(System.nanoTime())
operator fun Duration.plus(timer: ElapsedTime) = timer + this

/**
 * Utility function for calculating the d/dt
 */
fun ElapsedTime.deriv(current: Double, last: Double) = (last - current) / elapsed.toDouble(DurationUnit.SECONDS)