package com.escapevelocity.ducklib.command.scheduler

import com.escapevelocity.ducklib.command.commands.Command
import com.escapevelocity.ducklib.command.subsystem.Subsystem

/**
 * An interface that defines a command scheduler - a class that allows you to add commands, run added commands, and
 * cancel those commands
 */
interface CommandScheduler {
    val hasCommands: Boolean
    val commands: Collection<Command>
    val subsystems: Map<Subsystem, Command?>

    fun scheduleCommand(command: Command)

    fun cancelCommand(command: Command)

    fun run()

    fun addSubsystem(vararg subsystems: Subsystem)

    fun removeSubsystem(vararg subsystems: Subsystem)

    fun setDefaultCommand(vararg subsystems: Subsystem, command: Command?)

    fun Command.schedule() {
        scheduleCommand(this)
    }

    fun Command.cancel() {
        cancelCommand(this)
    }

    /**
     * Whether the command has a conflict or not (e.g. whether it can be scheduled and then will run on a single tick)
     */
    val Command.conflicts: Boolean

    /**
     * The conflicting command, if there is one
     */
    val Subsystem.command: Command?
}