package com.escapevelocity.ducklib.core.command.commands

abstract class RequirementCommand(vararg requirements: Any) : Command() {
    init {
        addRequirements(requirements)
    }
}