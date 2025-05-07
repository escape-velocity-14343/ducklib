package com.escapevelocity.ducklib.core.command.commands

import com.escapevelocity.ducklib.core.command.commands.composition.TimeoutCommand
import kotlin.time.Duration
import kotlin.time.DurationUnit

/**
 * Waits for a given duration while doing nothing, then finishes.
 *
 * @param duration How long it will wait for
 * @param trackSuspensionTime Whether the [TimeoutCommand] will subtract from the total time the amount of time
 * suspended
 */
open class WaitCommand(private val duration: Duration, private val trackSuspensionTime: Boolean = true) : Command() {
    private var timer = -1L
    private var suspensionTimer = -1L
    private var totalSuspensionTime = 0L

    private val totalTimer: Double
        get() = (System.nanoTime() - timer - totalSuspensionTime - if (suspensionTimer < 0) 0 else System.nanoTime() - suspensionTimer) / 1e9

    override fun initialize() {
        timer = System.nanoTime()
        suspensionTimer = -1L
        totalSuspensionTime = 0L
    }

    override fun end(canceled: Boolean) {
        timer = -1L
    }

    override fun suspend() {
        suspensionTimer = System.nanoTime()
    }

    override fun resume() {
        if (trackSuspensionTime) {
            totalSuspensionTime += System.nanoTime() - suspensionTimer
        }
        suspensionTimer = -1L
    }

    override val finished
        get() = totalTimer > duration.toDouble(DurationUnit.SECONDS)


    override fun toString(): String = super.toString() +
            if (timer > 0) " (%.3f / %.3f)".format(totalTimer, duration.toDouble(DurationUnit.SECONDS)) else ""
}