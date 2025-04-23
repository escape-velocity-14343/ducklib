package com.escapevelocity.ducklib.command.commands.group

import com.escapevelocity.ducklib.command.commands.Command

class SequentialCommandGroup(vararg commands: Command): CommandGroup(*commands) {
    private var _commands: ArrayList<Command>? = null
        get() {
            if (field == null) {
                field = ArrayList()
            }

            return field
        }
    override val commands: Collection<Command>
        get() = _commands!!

    private var currentCommand: Int = 0

    override fun addCommand(command: Command) {
        _commands!!.add(command)
        addRequirements(command.requirements)
    }

    override fun initialize() {
        super.initialize()
        currentCommand = 0
    }

    override fun execute() {
        if (currentCommand < 0 || currentCommand >= _commands!!.size) {
            return
        }

        val command = _commands!![currentCommand]
        command.execute()

        if (command.isFinished()) {
            command.end(false)
            currentCommand++
        }
    }

    override fun isFinished(): Boolean = currentCommand >= _commands!!.size
}