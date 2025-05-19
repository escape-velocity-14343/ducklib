package com.escapevelocity.ducklib.core.command.commands

/**
 * A command that waits until a condition is met.
 *
 * [Online documentation](https://escape-velocity-14343.github.io/ducklib/commands/builtin/#waituntilcommand)
 * @param condition The condition to wait for.
 */
class WaitUntilCommand(private val condition: () -> Boolean) : Command() {
    override val finished get() = condition()
}