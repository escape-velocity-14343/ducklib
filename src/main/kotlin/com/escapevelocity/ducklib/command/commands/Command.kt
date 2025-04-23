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

    /**
     * Called once, when the command is scheduled. Override to provide custom behavior
     */
    open fun initialize() {}

    /**
     * Called every time [Command.Scheduler.run] is called while the command is active
     */
    open fun execute() {}

    /**
     * Checked every time [Command.Scheduler.run] is called, after [Command.execute]. Commands are guaranteed to run
     * [Command.execute] once, even if this method returns `true`
     */
    open fun isFinished() = true

    /**
     * Called when the command is finished and the command scheduler is about to deschedule the command
     * @param interrupted If the command was interrupted, such as by calling [Command.Scheduler.cancel]
     */
    open fun end(interrupted: Boolean) {}

    fun <T : Command> T.setName(name: String): T {
        this.name = name
        return this
    }
}