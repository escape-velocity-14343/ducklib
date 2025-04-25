package com.escapevelocity.ducklib.command.scheduler

import com.escapevelocity.ducklib.command.commands.Command
import com.escapevelocity.ducklib.command.commands.Command.SubsystemConflictResolution
import com.escapevelocity.ducklib.command.subsystem.Subsystem
import com.escapevelocity.ducklib.util.containsAny

/**
 * The default ducklib scheduler. Does both the jobs of [CommandScheduler] and [TriggerScheduler]
 */
open class DuckyScheduler {
    companion object Scheduler : CommandScheduler, TriggerScheduler {
        override val hasCommands
            get() = scheduledCommands.size > 0
        override val commands: Collection<Command>
            get() = scheduledCommands
        override val subsystems: Map<Subsystem, Command?>
            get() = _subsystems

        protected val scheduledCommands = HashSet<Command>()
        private val queuedCommands = LinkedHashSet<Command>()
        private val commandsToCancel = ArrayList<Command>()
        private val commandRequirements = HashMap<Subsystem, Command>()
        private val _subsystems = HashMap<Subsystem, Command?>()
        private var runningCommands = false

        private val triggers = ArrayList<TriggeredAction>()

        override fun scheduleCommand(command: Command) {
            if (runningCommands && command !in queuedCommands) {
                queuedCommands.add(command)
                return
            }

            if (command in scheduledCommands) {
                return
            }

            // check for conflicts
            if (command.conflicts) {
                // if attempted to be scheduled command uses QUEUE conflict resolution, don't do anything
                if (command.conflictResolution == SubsystemConflictResolution.QUEUE) {
                    return
                }

                if (command.conflictResolution == SubsystemConflictResolution.CANCEL_THIS) {
                    queuedCommands.remove(command)
                    return
                }

                // otherwise, find the conflicting command(s) and deschedule it
                for (subsystem in command.requirements) commandRequirements[subsystem]?.cancel()
            }

            initCommand(command)
            queuedCommands.remove(command)
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
                    if (command.finished) {
                        command.end(false)
                        commandsToRemove.add(command)
                    }
                }
            } finally {
                runningCommands = false
            }

            for (command in commandsToRemove) command.remove()
            for (command in commandsToCancel) command.cancel()
            for (command in queuedCommands) command.schedule()

            triggers.filter { it.trigger() }.forEach { it.action() }

            for (subsystemCommand in _subsystems) {
                subsystemCommand.key.periodic()

                val cmd = subsystemCommand.value
                // if command exists, isn't already scheduled, and doesn't conflict, schedule it
                if (cmd != null && cmd !in scheduledCommands && subsystemCommand.key !in commandRequirements) {
                    cmd.schedule()
                }
            }
        }

        override fun addSubsystem(vararg subsystems: Subsystem) {
            for (subsystem in subsystems) {
                this._subsystems[subsystem] = null
            }
        }

        override fun removeSubsystem(vararg subsystems: Subsystem) {
            for (subsystem in subsystems) {
                this._subsystems.remove(subsystem)
                this._subsystems[subsystem]?.cancel()
            }
        }

        override fun setDefaultCommand(vararg subsystems: Subsystem, command: Command?) {
            for (subsystem in subsystems) {
                this._subsystems[subsystem] = command
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

        override val Command.conflicts
            get() = commandRequirements.keys.containsAny(this.requirements)

        override val Subsystem.command
            get() = commandRequirements.getOrDefault(this, null)

        override fun toString(): String {
            return """
DuckyScheduler
Scheduled commands:${scheduledCommands.joinToString(prefix = "\n").prependIndent()}
Registered subsystems: ${subsystems.entries.joinToString(prefix = "\n") { (ss, _) -> "${if (ss in commandRequirements) "🔒" else " "} ${ss.name}" }}
Queued commands:${queuedCommands.mapIndexed { it, i -> "\n$i: $it" }.joinToString().prependIndent()}
Subsystem commands:${subsystems.entries.joinToString(prefix = "\n").prependIndent()}
Triggers: ${triggers.joinToString().prependIndent()}
"""
        }

        data class TriggeredAction(val trigger: () -> Boolean, val action: () -> Unit)
    }
}