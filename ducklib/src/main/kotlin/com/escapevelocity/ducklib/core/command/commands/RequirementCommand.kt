package com.escapevelocity.ducklib.core.command.commands

/**
 * A helper base command that accepts a set of requirements as constructor arguments.
 */
abstract class RequirementCommand(vararg requirements: Any) : Command() {
    /**
     * A helper base command that accepts a set of requirements
     * (in the form of commands to duplicate) as constructor arguments.
     */
    constructor(vararg commands: Command) : this(commands.map(Command::requirements))
    init {
        addRequirements(requirements)
    }
}