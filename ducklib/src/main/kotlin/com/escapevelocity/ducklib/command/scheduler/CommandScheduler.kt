package com.escapevelocity.ducklib.command.scheduler

import com.escapevelocity.ducklib.command.commands.Command
import com.escapevelocity.ducklib.command.subsystem.Subsystem

/**
 * An interface that defines a command scheduler - a class that allows you to add commands, run added commands, and
 * cancel those commands
 */
interface CommandScheduler {
    /**
     * Whether the scheduler has currently scheduled commands.
     */
    val hasCommands: Boolean

    /**
     * A list of commands currently scheduled.
     */
    val commands: Collection<Command>

    /**
     * A list of the currently registered commands.
     */
    val subsystems: Map<Subsystem, Command?>

    /**
     * Schedule a command.
     *
     * **NOTE**: If the scheduler is currently running, the scheduling will be deferred until the scheduler is done
     * running.
     * @param command The command to schedule
     */
    fun scheduleCommand(command: Command)

    /**
     * Cancel a command.
     *
     * **NOTE**: If the scheduler is currently running, the cancellation will be deferred until the scheduler is done running.
     * @param command The command to cancel
     */
    fun cancelCommand(command: Command)

    /**
     * Execute a single tick of the scheduler.
     */
    fun run()

    /**
     * Register a subsystem, or multiple subsystems
     * @param subsystems Zero or more subsystems to register
     */
    fun addSubsystem(vararg subsystems: Subsystem)

    /**
     * Deregister a subsystem, or multiple subsystems
     * @param subsystems Zero or more subsystems to deregister
     */
    fun removeSubsystem(vararg subsystems: Subsystem)

    /**
     * Set the default command of a subsystem.
     *
     * Default commands of a subsystem will run forever, and must require that subsystem. Command overriding behavior is
     * controlled by [Command.ConflictResolution], so make sure that a default command has a
     * [Command.conflictResolution] of [Command.ConflictResolution.CANCEL_THIS] or
     * [Command.ConflictResolution.QUEUE] (it doesn't really matter since if a default command gets kicked off
     * the scheduled commands, it'll just keep trying to get rescheduled every tick)
     *
     * **NOTE**: A default command will only get overridden if the other command has a [Command.conflictResolution] of
     * [Command.ConflictResolution.CANCEL_OTHER].
     *
     * **NOTE**: Unlike in FTCLib, default commands will simply be rescheduled if they finish. Their [Command.end]
     * method *will* be called.
     * @param subsystem The subsystem to associate the default command with
     * @param command The default command. If null, no default command will be associated with the subsystem
     */
    fun setDefaultCommand(subsystem: Subsystem, command: Command?)

    /**
     * Reset the state of the scheduler
     */
    fun reset()

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