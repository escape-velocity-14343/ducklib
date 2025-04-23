package com.escapevelocity.ducklib.command.commands

class WaitCommand(val seconds: Double): Command() {
    private var timer: Long = 0

    override fun initialize() {
        timer = System.nanoTime()
    }

    override fun isFinished(): Boolean = (System.nanoTime() - timer) / 1e9 > seconds
}