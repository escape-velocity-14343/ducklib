package com.escapevelocity.ducklib.command.commands

class WaitCommand(private val seconds: Double): Command() {
    private var timer: Long = 0

    override fun initialize() {
        timer = System.nanoTime()
    }

    override val finished
        get() = (System.nanoTime() - timer) / 1e9 > seconds

    override fun toString(): String = "${super.toString()} (${(System.nanoTime() - timer) / 1e9} / ${seconds})"
}