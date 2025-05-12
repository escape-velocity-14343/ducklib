package com.escapevelocity.ducklib.core.command.commands.composition

import com.escapevelocity.ducklib.core.command.commands.Command
import com.escapevelocity.ducklib.core.command.commands.Priority
import kotlin.collections.flatten

/**
 * A utility class that accepts any number of commands.
 * By default, it has
 * * The union of all the command's requirements
 * * The highest priority of all the commands
 * * Suspendability only if all the composed commands are suspendable
 *
 * It also includes utility overrides of [initialize], [execute],
 * [suspend], [resume], and [end] that delegate functionality to all the composed commands.
 *
 * **NOTE**:
 * This command works best if you know which commands will be going in it at constructor time!
 *
 * **NOTE**:
 * This does not override [finished]!
 * Implement that yourself.
 */
abstract class CompositionCommand(protected vararg val commands: Command) : Command() {
    init {
        commands.forEach { it.composed = true }
    }

    override val requirements: Set<Any> = commands.flatMap { it.requirements }.toSet()
    override var priority: Priority = commands.maxOf { it.priority }
    override val suspendable: Boolean = commands.all { it.suspendable }

    override fun initialize() {
        for (command in commands) command.initialize()
    }

    override fun execute() {
        for (command in commands) command.execute()
    }

    override fun suspend() {
        for (command in commands) command.suspend()
    }

    override fun resume() {
        for (command in commands) command.resume()
    }

    override fun end(canceled: Boolean) {
        for (command in commands) command.end(canceled)
    }

    override fun toString() = "${super.toString()} $stringPostfix"

    protected open val stringPostfix get() = "[${commands.joinToString()}]"
}