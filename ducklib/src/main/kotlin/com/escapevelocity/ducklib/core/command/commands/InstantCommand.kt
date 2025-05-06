package com.escapevelocity.ducklib.core.command.commands

class InstantCommand(vararg requirements: Any, val toRun: () -> Unit) : RequirementCommand(requirements) {
    override fun execute() {
        toRun()
    }
}

fun (() -> Unit).instant(vararg requirements: Any) = InstantCommand(requirements, toRun = this)