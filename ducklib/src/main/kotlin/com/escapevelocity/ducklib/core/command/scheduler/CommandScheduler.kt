package com.escapevelocity.ducklib.core.command.scheduler

import com.escapevelocity.ducklib.core.command.commands.Command
import com.escapevelocity.ducklib.core.command.subsystem.Subsystem

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
    val subsystems: Set<Subsystem>

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
     * **NOTE**: If the scheduler is currently running, the cancellation will be deferred until the scheduler is done
     * running.
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
     * Reset the state of the scheduler
     */
    fun reset()

    /**
     * Schedule a command.
     *
     * **NOTE**: If the scheduler is currently running, the scheduling will be deferred until the scheduler is done
     * running.
     * @param command The command to schedule
     * @sample com.escapevelocity.ducklib.core.samples.implicitCommandSchedulerSample
     * @sample com.escapevelocity.ducklib.core.samples.explicitCommandSchedulerSample
     */
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