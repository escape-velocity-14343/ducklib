package com.escapevelocity.ducklib.command.scheduler

import com.escapevelocity.ducklib.command.commands.Command
import com.escapevelocity.ducklib.command.commands.Command.SubsystemConflictResolution
import com.escapevelocity.ducklib.command.subsystem.Subsystem
import util.containsAny

/**
 * The default ducklib scheduler. Does both the jobs of [CommandScheduler] and [TriggerScheduler]
 */
open class DuckyScheduler {
    companion object Scheduler: CommandScheduler, TriggerScheduler {
        override val hasCommands
            get() = scheduledCommands.size > 0

        protected val scheduledCommands = HashSet<Command>()
        private val commandsToSchedule = ArrayList<Command>()
        private val commandsToCancel = ArrayList<Command>()
        private val commandRequirements = HashMap<Subsystem, Command>()
        private val subsystems = HashMap<Subsystem, Command?>()
        private var runningCommands = false

        private val triggers = ArrayList<TriggeredAction>()

        override fun scheduleCommand(command: Command) {
            if (runningCommands) {
                commandsToSchedule.add(command)
                return
            }

            // check for conflicts
            if (command.conflicts()) {
                // if attempted to be scheduled command uses CANCEL_THIS conflict resolution, don't do anything
                if (command.conflictResolution == SubsystemConflictResolution.CANCEL_THIS) {
                    return
                }

                // otherwise, find the conflicting command(s) and deschedule it
                for (subsystem in command.requirements) commandRequirements[subsystem]?.cancel()
            }

            initCommand(command)
        }

        override fun cancelCommand(command: Command) {
            if (runningCommands) {
                commandsToCancel.add(command)
                return
            }

            if (command !in scheduledCommands) {
                return
            }

            command.end(true)
            removeCommand(command)
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

            for (command in commandsToRemove) command.remove()
            for (command in commandsToCancel) command.cancel()
            for (command in commandsToSchedule) command.schedule()

            triggers.filter { it.trigger() }.forEach { it.action() }

            for (subsystemCommand in subsystems) {
                subsystemCommand.key.periodic()

                val cmd = subsystemCommand.value
                // if command exists, isn't already scheduled, and doesn't conflict, schedule it
                if (cmd != null && cmd !in scheduledCommands && !cmd.conflicts()) {
                    cmd.schedule()
                }
            }
        }

        override fun addSubsystem(vararg subsystems: Subsystem) {
            for (subsystem in subsystems) {
                this.subsystems[subsystem] = null
            }
        }

        override fun removeSubsystem(vararg subsystems: Subsystem) {
            for (subsystem in subsystems) {
                this.subsystems.remove(subsystem)
                this.subsystems[subsystem]?.cancel()
            }
        }

        override fun setDefaultCommand(vararg subsystems: Subsystem, command: Command?) {
            for (subsystem in subsystems) {
                this.subsystems[subsystem] = command
            }
        }

        private fun removeCommand(command: Command) {
            scheduledCommands.remove(command)
            commandRequirements.keys.removeAll(command.requirements)
        }

        private fun initCommand(command: Command) {
            scheduledCommands.add(command)
            for (subsystem in command.requirements) commandRequirements[subsystem] = command
            command.initialize()
        }

        private fun Command.remove() {
            removeCommand(this)
        }

        private fun Command.conflicts() = commandRequirements.keys.containsAny(this.requirements)

        data class TriggeredAction(val trigger: () -> Boolean, val action: () -> Unit)
    }
}