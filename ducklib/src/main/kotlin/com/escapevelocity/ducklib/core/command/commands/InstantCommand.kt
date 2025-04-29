package com.escapevelocity.ducklib.core.command.commands

class InstantCommand(val toRun: () -> Unit): Command() {
    override fun execute() {
        toRun()
    }
}