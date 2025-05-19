package com.escapevelocity.ducklib.core.command.commands.composition

import com.escapevelocity.ducklib.core.command.commands.Command

/**
 * Runs [command] if [shouldRun] is true, otherwise it doesn't do anything.
 *
 * [Online documentation](https://escape-velocity-14343.github.io/ducklib/commands/groups/#ifcommand-ifelsecommand-and-whencommand)
 */
class IfCommand(private val shouldRun: () -> Boolean, private val command: Command) : CompositionCommand(command) {
    var runningCommand: Command? = null

    override fun initialize() {
        runningCommand = if (shouldRun()) command else null
        runningCommand?.initialize()
    }

    override fun execute() {
        runningCommand?.execute()
    }

    override val finished: Boolean
        get() = runningCommand?.finished ?: true

    override fun suspend() {
        runningCommand?.suspend()
    }

    override fun resume() {
        runningCommand?.resume()
    }

    override fun end(canceled: Boolean) {
        runningCommand?.end(canceled)
    }
}