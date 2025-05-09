package com.escapevelocity.ducklib.core.command.commands

/**
 * A command that does nothing.
 *
 * It can be extended for convenience because it's got a constructor that accepts a set of requirements.
 */
open class NoOpCommand(vararg requirements: Any) : Command() {
    /**
     * A helper base command that accepts a set of requirements
     * (in the form of commands to duplicate) as constructor arguments.
     */
    constructor(vararg commands: Command) : this(commands.map(Command::requirements))
    constructor() : this(*emptyArray<Any>())
    init {
        addRequirements(requirements)
    }
}