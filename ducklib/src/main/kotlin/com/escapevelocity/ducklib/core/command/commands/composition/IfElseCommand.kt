package com.escapevelocity.ducklib.core.command.commands.composition

import com.escapevelocity.ducklib.core.command.commands.Command

/**
 * Runs a [trueCommand] if [shouldRun] returns true, otherwise it runs [falseCommand].
 */
class IfElseCommand(
    private val shouldRun: () -> Boolean, private val trueCommand: Command, private val falseCommand: Command
) : CompositionCommand(trueCommand, falseCommand) {
    var runningCommand: Command? = null

    override fun initialize() {
        runningCommand = if (shouldRun()) trueCommand else falseCommand
        runningCommand?.initialize()
    }

    override fun execute() {
        runningCommand?.execute()
    }

    override fun suspend() {
        runningCommand?.suspend()
    }

    override fun resume() {
        runningCommand?.resume()
    }

    override fun end(canceled: Boolean) {
        runningCommand?.end(canceled)
    }

    override val stringPostfix: String
        get() = "[\n${
            commands
                .joinToString(separator = "\n") { command ->
                    "${if (command == runningCommand) " > " else " "}$command"
                }.prependIndent()
        }\n]"
}