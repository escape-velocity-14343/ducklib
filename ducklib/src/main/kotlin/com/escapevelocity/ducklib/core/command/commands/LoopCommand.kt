package com.escapevelocity.ducklib.core.command.commands

class LoopCommand(vararg requirements: Any, val loop: () -> Unit) : RequirementCommand(requirements) {
    override fun execute() {
        loop()
    }

    override val finished = false
}