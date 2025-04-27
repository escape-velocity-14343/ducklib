package com.escapevelocity.ducklib.command.scheduler

import com.escapevelocity.ducklib.command.commands.Command
import com.escapevelocity.ducklib.command.commands.Command.SubsystemConflictResolution
import com.escapevelocity.ducklib.command.subsystem.Subsystem
import com.escapevelocity.ducklib.command.trigger.Trigger
import com.escapevelocity.ducklib.util.b16Hash
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
        private val lastTriggerValues = HashMap<TriggeredAction, Boolean?>()
        private val lastTriggerNanos = HashMap<TriggeredAction, Long?>()

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
                    queuedCommands.add(command)
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

        override fun bind(trigger: () -> Boolean, originalTrigger: Trigger?, action: () -> Unit) {
            val ta = TriggeredAction(trigger, originalTrigger, action)
            triggers.add(ta)
            lastTriggerNanos[ta] = null
            lastTriggerValues[ta] = null
        }

        /**
         * Execute a single tick of the scheduler.
         *
         * This updates all the commands, including scheduling queued commands, rescheduling unrequired and unscheduled
         * subsystem commands, and updating trigger states.
         */
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

            for (ta in triggers) {
                val triggerVal = ta.trigger()
                if (triggerVal) {
                    ta.action()
                    lastTriggerNanos[ta] = System.nanoTime()
                }
                lastTriggerValues[ta] = triggerVal
            }

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

        override fun setDefaultCommand(subsystem: Subsystem, command: Command?) {
            this._subsystems[subsystem] = command
        }

        /**
         * Reset the state of the scheduler
         */
        override fun reset() {
            if (runningCommands) {
                throw ConcurrentModificationException("Hey! Please do not reset the scheduler while it is running!")
            }

            scheduledCommands.clear()
            queuedCommands.clear()
            commandsToCancel.clear()
            commandRequirements.clear()
            _subsystems.clear()
            triggers.clear()
            lastTriggerNanos.clear()
            lastTriggerValues.clear()
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
Triggers:
${
                triggers.joinToString("\n").prependIndent()
            }
Registered subsystems:
${
                subsystems.entries.joinToString(
                    "\n",
                ) { (ss, _) -> "${if (ss in commandRequirements) "×" else " "}${ss.name}" }.prependIndent()
            }
Subsystem commands:
${
                subsystems.entries.joinToString("\n") { (ss, cmd) ->
                    "$ss${if (cmd in scheduledCommands) ">>>" else " - "}$cmd"
                }.prependIndent()
            }
Scheduled commands:
${scheduledCommands.joinToString("\n").prependIndent()}
Queued commands:
${queuedCommands.mapIndexed { it, i -> "$i: $it" }.joinToString("\n").prependIndent()}
"""
        }

        data class TriggeredAction(
            val trigger: () -> Boolean,
            val originalTrigger: Trigger? = null,
            val action: () -> Unit
        ) {
            override fun toString(): String {
                val sb = StringBuilder()
                if (originalTrigger != null) {
                    sb.append(originalTrigger)
                } else {
                    sb.append("Trigger@")
                    sb.append(b16Hash())
                }
                val lastTriggerValue = lastTriggerValues[this]
                if (lastTriggerValue != null) {
                    sb.append(" [")
                    sb.append(if (lastTriggerValue) "x" else " ")
                    sb.append("]")
                }
                val lt = lastTriggerNanos[this]
                if (lt != null) {
                    sb.append(" (%.3f)".format((System.nanoTime() - lt) / 1e9))
                }
                return sb.toString()
            }
        }

        val (() -> Boolean).trigger
            get() = Trigger(this@Scheduler, this@Scheduler, this)
    }
}