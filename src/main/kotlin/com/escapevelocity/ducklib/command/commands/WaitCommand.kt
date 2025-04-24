package com.escapevelocity.ducklib.command.commands

class WaitCommand(seconds: Double): Command() {
    private var timer: Long = 0

    override fun initialize() {
        timer = System.nanoTime()
    }

    override val finished = (System.nanoTime() - timer) / 1e9 > seconds
}