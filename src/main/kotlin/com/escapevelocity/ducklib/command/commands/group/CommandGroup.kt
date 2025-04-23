package com.escapevelocity.ducklib.command.commands.group

import com.escapevelocity.ducklib.command.commands.Command

abstract class CommandGroup(vararg commands: Command) : Command() {
    protected abstract val commands: Collection<Command>

    init {
        addCommands(*commands)
    }

    protected fun addCommands(vararg commands: Command) = commands.forEach(this::addCommand)

    abstract fun addCommand(command: Command)

    override fun initialize() {
        commands.forEach { it.initialize() }
    }

    override fun end(interrupted: Boolean) {
        commands.forEach { it.end(interrupted) }
    }
}