package com.escapevelocity.ducklib.command.commands

class InstantCommand(val toRun: () -> Unit): Command() {
    override fun execute() {
        toRun()
    }
}