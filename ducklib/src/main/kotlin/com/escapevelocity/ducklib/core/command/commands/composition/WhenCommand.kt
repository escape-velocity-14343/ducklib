package com.escapevelocity.ducklib.core.command.commands.composition

import com.escapevelocity.ducklib.core.command.commands.Command
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler.Companion.commands

/**
 * Selects between the commands provided in [commands] using the result from [selector].
 *
 * [Online documentation](https://escape-velocity-14343.github.io/ducklib/commands/groups/#ifcommand-ifelsecommand-and-whencommand)
 * @sample com.escapevelocity.ducklib.core.samples.whenCommandSample
 */
class WhenCommand<TKey>(
    vararg commands: Pair<TKey, Command>,
    var default: Command? = null,
    private val selector: () -> TKey
) : Command() {
    private val _commands: MutableMap<TKey, Command> = mutableMapOf()
    private lateinit var runningCommand: Command

    init {
        _commands.putAll(commands)
    }

    override val requirements = _commands.values.map { it.requirements }.flatten().toMutableSet()

    operator fun get(key: TKey): Command? = _commands[key]
    operator fun set(key: TKey, value: Command) {
        _commands[key] = value
        value.composed = true
        addRequirements(value.requirements)
    }

    override fun initialize() {
        val state = selector()
        runningCommand = _commands[state] ?: default
                ?: error("State $state was not found in the command list, and no default was set")
        runningCommand.initialize()
    }

    override fun execute() {
        runningCommand.execute()
    }

    override fun suspend() {
        runningCommand.suspend()
    }

    override fun resume() {
        runningCommand.resume()
    }

    override fun end(canceled: Boolean) {
        runningCommand.end(canceled)
    }

    override val finished
        get() = runningCommand.finished

    override fun toString() = "${super.toString()} [\n${
        _commands.entries
            .joinToString(separator = "\n") { (state, command) ->
                "${if (command == runningCommand) ">" else " "}$state: $command"
            }
            .prependIndent()
    }\n]"
}