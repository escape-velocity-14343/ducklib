package com.escapevelocity.ducklib.core.command.commands.composition.group

import com.escapevelocity.ducklib.core.command.commands.Command
import com.escapevelocity.ducklib.core.util.b16Hash

/**
 * The base CommandGroup implementation.
 *
 * Includes functions for adding commands,
 * verifying that all commands are ungrouped,
 * and stringification.
 *
 * @sample [com.escapevelocity.ducklib.core.samples.commandGroupSample]
 */
abstract class CommandGroup(vararg commands: Command) : Command() {
    protected abstract val commands: Collection<Command>

    init {
        addCommands(*commands)
    }

    override val suspendable: Boolean
        get() = commands.all { it.suspendable }

    /**
     * Add a list of commands to the group
     * @param commands The commands to add
     */
    fun addCommands(vararg commands: Command) = commands.forEach {
        if (it.composed) throw IllegalArgumentException("Grouped command $it cannot be regrouped")
        it.composed = true
        addCommand(it)
    }

    /**
     * Adds a single command.
     * @param command The command to add
     */
    protected abstract fun addCommand(command: Command)

    override fun initialize() = commands.forEach { it.initialize() }

    override fun end(canceled: Boolean) = commands.forEach { it.end(canceled) }

    override fun suspend() = commands.forEach { it.suspend() }

    override fun resume() = commands.forEach { it.resume() }

    override fun toString(): String =
        "$name${if (name == javaClass.simpleName) "" else " (${javaClass.simpleName})"}@${this.b16Hash()} [${
            commands.mapIndexed { i, cmd -> "\n${cmd.prefix()}$i: $cmd" }.joinToString("").prependIndent()
        }\n]"

    /**
     * The prefix to use when stringifying.
     */
    protected open fun Command.prefix(): String = ""
}