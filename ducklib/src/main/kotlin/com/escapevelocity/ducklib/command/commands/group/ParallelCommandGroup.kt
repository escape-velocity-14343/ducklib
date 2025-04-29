package com.escapevelocity.ducklib.command.commands.group

import com.escapevelocity.ducklib.command.commands.Command
import com.escapevelocity.ducklib.util.containsAny
import java.security.InvalidParameterException

open class ParallelCommandGroup(vararg commands: Command) : CommandGroup(*commands) {

    protected var _commands: LinkedHashMap<Command, Boolean>? = null
        get() {
            if (field == null) {
                field = LinkedHashMap()
            }

            return field
        }
    final override val commands
        get() = _commands!!.keys

    override fun initialize() {
        commands.forEach { it.initialize() }
        for (key in commands) {
            _commands!![key] = false
        }
    }

    override fun execute() {
        for (command in commands) {
            if (_commands!![command] == true) {
                continue
            }

            command.execute()

            if (command.finished) {
                command.end(false)
                _commands!![command] = true
            }
        }
    }

    override val finished
        get() = _commands!!.values.all { it }

    override fun addCommand(command: Command) {
        if (requirements.containsAny(command.requirements)) {
            throw InvalidParameterException("Commands in a command group must not require the same subsystems")
        }

        _commands!![command] = false
        addRequirements(command.requirements)
    }

    override fun end(interrupted: Boolean) = commands.forEach { it.end(interrupted) }

    override fun Command.prefix() = if (_commands!![this] == false) ">" else " "
}