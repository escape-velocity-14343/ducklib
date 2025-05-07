package com.escapevelocity.ducklib.core.command.commands.composition.group

import com.escapevelocity.ducklib.core.command.commands.Command

/**
 * Runs the given commands one at a time in the same order they're added.
 *
 * Commands *can* share requirements, unlike [ParallelCommandGroup], given that they each run separately.
 *
 * @sample [com.escapevelocity.ducklib.core.samples.sequentialCommandGroupSample]
 */
open class SequentialCommandGroup(vararg commands: Command) : CommandGroup(*commands) {
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
        currentCommand = 0

        if (_commands!!.size > 0) {
            _commands!!.first().initialize()
        }
    }

    override fun execute() {
        if (currentCommand < 0 || currentCommand >= _commands!!.size) {
            return
        }

        val command = _commands!![currentCommand]
        command.execute()

        if (command.finished) {
            command.end(false)
            currentCommand++
            if (currentCommand < _commands!!.size) {
                _commands!![currentCommand].initialize()
            }
        }
    }

    override val finished
        get() = currentCommand >= _commands!!.size

    override fun Command.prefix(): String =
        if (currentCommand < _commands!!.size && this == _commands!![currentCommand]) ">" else " "
}