package com.escapevelocity.ducklib.command.commands

class WaitCommand(private val seconds: Double, private val trackSuspensionTime: Boolean = true) : Command() {
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

    override fun end(interrupted: Boolean) {
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
        get() = totalTimer > seconds


    override fun toString(): String = super.toString() +
            if (timer > 0) " (%.3f / %.3f)".format(totalTimer, seconds) else ""
}