package com.escapevelocity.ducklib.core.command.commands

/**
 * A command that does nothing.
 *
 * It can be extended for convenience because it's got a constructor that accepts a set of requirements.
 *
 * [Online documentation](https://escape-velocity-14343.github.io/ducklib/commands/builtin/#noopcommand)
 */
open class NoOpCommand(vararg requirements: Any) : Command() {
    constructor() : this(*emptyArray<Any>())
    init {
        addRequirements(*requirements)
    }
}