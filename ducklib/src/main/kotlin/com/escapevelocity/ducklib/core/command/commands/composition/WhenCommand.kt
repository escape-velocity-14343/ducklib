package com.escapevelocity.ducklib.core.command.commands.composition

import com.escapevelocity.ducklib.core.command.commands.Command

/**
 * Selects between the commands provided in [commands] using the result from [selector].
 *
 * @sample com.escapevelocity.ducklib.core.samples.whenCommandSample
 */
class WhenCommand<TKey>(vararg commands: Pair<TKey, Command>, private val selector: () -> TKey) : Command() {
    private val _commands: MutableMap<TKey, Command> = mutableMapOf()

    init {
        _commands.putAll(commands)
    }

    override val requirements = _commands.values.map { it.requirements }.flatten().toMutableSet()

    operator fun get(key: TKey): Command? = _commands[key]
    operator fun set(key: TKey, value: Command) {
        _commands[key] = value
        addRequirements(value.requirements)
    }

    lateinit var runningCommand: Command

    override fun initialize() {
        val state = selector()
        runningCommand = _commands[selector()] ?: error("State $state was not found in the command list")
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