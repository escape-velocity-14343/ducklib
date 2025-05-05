package com.escapevelocity.ducklib.core.command.commands

class InstantCommand(vararg requirements: Any, val toRun: () -> Unit) : RequirementCommand(requirements) {
    override fun execute() {
        toRun()
    }
}