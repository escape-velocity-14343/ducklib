package com.escapevelocity.ducklib.command.commands

class WaitCommand(private val seconds: Double) : Command() {
    private var timer = -1L

    override fun initialize() {
        timer = System.nanoTime()
    }

    override fun end(interrupted: Boolean) {
        timer = -1L
    }

    override val finished
        get() = (System.nanoTime() - timer) / 1e9 > seconds

    override fun toString(): String = super.toString() +
            if (timer > 0) " (${(System.nanoTime() - timer) / 1e9} / ${seconds})" else ""
}