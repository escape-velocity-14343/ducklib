package com.escapevelocity.ducklib.command.scheduler

import com.escapevelocity.ducklib.command.commands.Command
import com.escapevelocity.ducklib.command.commands.Command.SubsystemConflictResolution
import com.escapevelocity.ducklib.command.subsystem.Subsystem
import util.containsAny
import java.util.function.BooleanSupplier

/**
 * The default ducklib scheduler. Does both the jobs of [CommandScheduler] and [TriggerScheduler]
 */
open class DuckyScheduler : CommandScheduler, TriggerScheduler {
    private val scheduledCommands = HashSet<Command>()
    private val commandsToSchedule = ArrayList<Command>()
    private val commandsToCancel = ArrayList<Command>()
    private val commandRequirements = HashMap<Subsystem, Command>()
    private var runningCommands = false

    private val triggers = ArrayList<TriggeredAction>()

    override fun schedule(command: Command) {
        if (runningCommands) {
            commandsToSchedule.add(command)
            return
        }

        // check for conflicts
        if (commandRequirements.keys.containsAny(command.requirements)) {
            // if attempted to be scheduled command uses CANCEL_THIS conflict resolution, don't do anything
            if (command.conflictResolution == SubsystemConflictResolution.CANCEL_THIS) {
                return
            }

            // otherwise, find the conflicting command(s) and deschedule it
            for (subsystem in command.requirements) commandRequirements[subsystem]?.cancelCommand()
        }

        initCommand(command)
    }

    override fun cancel(command: Command) {
        if (runningCommands) {
            commandsToCancel.add(command)
            return
        }

        if (command !in scheduledCommands) {
            return
        }

        command.end(true)
        remove(command)
    }

    override fun bind(trigger: () -> Boolean, action: () -> Unit) {
        triggers.add(TriggeredAction(trigger, action))
    }

    override fun run() {
        runningCommands = true
        val commandsToRemove = ArrayList<Command>()
        try {
            for (command in scheduledCommands) {
                command.execute()
                if (command.isFinished()) {
                    command.end(false)
                    commandsToRemove.add(command)
                }
            }
        } finally {
            runningCommands = false
        }

        for (command in commandsToRemove) command.removeCommand()
        for (command in commandsToCancel) command.cancelCommand()
        for (command in commandsToSchedule) command.scheduleCommand()

        triggers.filter { it.trigger() }.forEach { it.action() }
    }

    private fun remove(command: Command) {
        scheduledCommands.remove(command)
        commandRequirements.keys.removeAll(command.requirements)
    }

    private fun initCommand(command: Command) {
        scheduledCommands.add(command)
        for (subsystem in command.requirements) commandRequirements[subsystem] = command
        command.initialize()
    }

    private fun Command.removeCommand() {
        remove(this)
    }

    data class TriggeredAction(val trigger: () -> Boolean, val action: () -> Unit)
}