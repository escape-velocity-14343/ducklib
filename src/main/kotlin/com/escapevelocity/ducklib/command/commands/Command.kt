package com.escapevelocity.ducklib.command.commands

import com.escapevelocity.ducklib.command.subsystem.Subsystem
import util.containsAny
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

abstract class Command {
    enum class SubsystemConflictResolution {
        CANCEL_THIS,
        CANCEL_OTHER,
    }

    private val _requirements: MutableSet<Subsystem> = HashSet()
    val requirements: Set<Subsystem>
        get() = _requirements
    var inGroup = false
        protected set

    var name: String = this.javaClass.name

    open val conflictResolution = SubsystemConflictResolution.CANCEL_OTHER

    /**
     * Adds a set of requirements to the command. If a command's requirements interfere with another scheduled command's
     * requirements, the old command will be descheduled and the new command will take its place.
     * @param subsystems The subsystems to be required
     */
    protected fun addRequirements(vararg subsystems: Subsystem) {
        _requirements.addAll(subsystems)
    }

    /**
     * Adds a set of requirements to the command. If a command's requirements interfere with another scheduled command's
     * requirements, the old command will be descheduled and the new command will take its place.
     * @param subsystems The subsystems to be required
     */
    protected fun addRequirements(subsystems: Collection<Subsystem>) {
        _requirements.addAll(subsystems)
    }

    open fun initialize() {}

    open fun execute() {}

    open fun isFinished() = true

    open fun end(interrupted: Boolean) {}

    fun <T : Command> T.setName(name: String): T {
        this.name = name
        return this
    }

    companion object Scheduler {
        private val scheduledCommands = HashSet<Command>()
        private val commandsToSchedule = ArrayList<Command>()
        private val commandsToCancel = ArrayList<Command>()
        private val commandRequirements = HashMap<Subsystem, Command>()
        private var runningCommands = false

        fun schedule(command: Command) {
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

        fun cancel(command: Command) {
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

        fun run() {
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

        fun Command.scheduleCommand() {
            schedule(this)
        }

        fun Command.cancelCommand() {
            cancel(this)
        }
    }
}