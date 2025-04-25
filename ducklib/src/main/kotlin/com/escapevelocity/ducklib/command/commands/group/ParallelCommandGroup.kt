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
        _commands!!.forEach { it.key.initialize() }
        for (key in _commands!!.keys) {
            _commands!![key] = false
        }
    }

    override fun execute() {
        for (command in _commands!!) {
            if (command.value) {
                continue
            }

            command.key.execute()

            if (command.key.finished) {
                command.key.end(false)
                _commands!![command.key] = true
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

    override fun end(interrupted: Boolean) {
        _commands!!.forEach { it.key.end(interrupted) }
    }
}