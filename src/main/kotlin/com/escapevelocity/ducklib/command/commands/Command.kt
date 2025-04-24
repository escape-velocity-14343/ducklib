package com.escapevelocity.ducklib.command.commands

import com.escapevelocity.ducklib.command.subsystem.Subsystem
import com.escapevelocity.ducklib.util.b16Hash

abstract class Command {
    enum class SubsystemConflictResolution {
        CANCEL_THIS,
        CANCEL_OTHER,
        QUEUE,
    }

    private val _requirements: MutableSet<Subsystem> = HashSet()
    val requirements: Set<Subsystem>
        get() = _requirements
    var inGroup = false
        protected set

    internal var _name: String = javaClass.simpleName
    open val name: String
        get() = _name

    open val conflictResolution = SubsystemConflictResolution.QUEUE

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
     * Called every time [com.escapevelocity.ducklib.command.scheduler.CommandScheduler.run] is called while the command is active
     */
    open fun execute() {}

    /**
     * Checked every time [com.escapevelocity.ducklib.command.scheduler.CommandScheduler.run] is called, after [Command.execute]. Commands are guaranteed to run
     * [Command.execute] once, even if this method returns `true`
     */
    open val finished = true

    /**
     * Called when the command is finished and the command scheduler is about to deschedule the command
     * @param interrupted If the command was interrupted, such as by calling [com.escapevelocity.ducklib.command.scheduler.CommandScheduler.cancel]
     */
    open fun end(interrupted: Boolean) {}


    override fun toString(): String = "$name${if(name == javaClass.simpleName) "" else " (${javaClass.simpleName})"}@${this.b16Hash()}"
}

fun <T : Command> T.setName(name: String): T {
    this._name = name
    return this
}
