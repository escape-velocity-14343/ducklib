package com.escapevelocity.ducklib.command.commands

import com.escapevelocity.ducklib.command.commands.Command.Priority.LOWEST
import com.escapevelocity.ducklib.util.b16Hash

abstract class Command {
    enum class ConflictResolution {
        CANCEL_ON_LOWER,
        QUEUE_ON_LOWER,
    }

    private val _requirements: MutableSet<Any> = HashSet()
    val requirements: Set<Any>
        get() = _requirements
    var inGroup = false
        protected set

    open var name: String = javaClass.simpleName

    var conflictResolution = ConflictResolution.QUEUE_ON_LOWER
    var priority = LOWEST

    /**
     * Whether this command is suspendable or not.
     *
     * A suspendable command will get suspended if another conflicting command gets scheduled. As a result, [run] will
     * no longer get called. [suspend] will be called once when the command is suspended, and [resume] will be called
     * once when the command is ready to be resumed again.
     */
    open val suspendable: Boolean = true

    /**
     * Adds a set of requirements to the command.
     *
     * If a command's requirements interfere with another scheduled command's requirements, the old command will be
     * descheduled and the new command will take its place depending on the priority rules.
     * @param requirements The objects to be required
     */
    fun addRequirements(vararg requirements: Any) {
        _requirements.addAll(requirements)
    }

    /**
     * Adds a set of requirements to the command.
     *
     * If a command's requirements interfere with another scheduled command's requirements, the old command will be
     * descheduled and the new command will take its place depending on the priority rules.
     * @param requirements The objects to be required
     */
    fun addRequirements(requirements: Collection<Any>) {
        _requirements.addAll(requirements)
    }

    /**
     * Removes a set of requirements to the command.
     *
     * If a command's requirements interfere with another scheduled command's requirements, the old command will be
     * descheduled and the new command will take its place depending on the priority rules.
     * @param requirements The objects to be no longer required
     */
    fun removeRequirements(vararg subsystems: Any) {
        _requirements.removeAll(subsystems)
    }

    /**
     * Removes a set of requirements to the command.
     *
     * If a command's requirements interfere with another scheduled command's requirements, the old command will be
     * descheduled and the new command will take its place depending on the priority rules.
     * @param requirements The objects to be no longer required
     */
    fun removeRequirements(subsystems: Collection<Any>) {
        _requirements.removeAll(subsystems)
    }

    /**
     * Called once when the command is scheduled. Override to provide custom behavior
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
     * Called when the command scheduler suspends this command.
     *
     * The command scheduler will only suspend a command if [suspendable] is `true`, otherwise if a conflicting command
     * with higher priority gets scheduled this command will get descheduled.
     */
    open fun suspend() {}

    /**
     * Called when the command scheduler resumes this command.
     */
    open fun resume() {}

    /**
     * Called when the command is finished and the command scheduler is about to deschedule the command
     * @param interrupted If the command was interrupted, such as by calling [com.escapevelocity.ducklib.command.scheduler.CommandScheduler.cancel]
     */
    open fun end(interrupted: Boolean) {}


    override fun toString(): String =
        "$name${if (name == javaClass.simpleName) "" else " (${javaClass.simpleName})"}@${this.b16Hash()}"

    object Priority {
        const val LOWEST = Int.MIN_VALUE
    }
}

/**
 * Sets the [name] of the [Command].
 *
 * @return The command with the same type to facilitate chaining
 */
fun <T : Command> T.setName(name: String): T {
    this.name = name
    return this
}

/**
 * Sets the [priority] of the [Command].
 *
 * @return The command with the same type to facilitate chaining
 */
fun <T : Command> T.setPriority(priority: Int): T {
    this.priority = priority
    return this
}

/**
 * Sets the [conflictResolution] of the [Command].
 *
 * @return The command with the same type to facilitate chaining
 */
fun <T : Command> T.setConflictResolution(conflictResolution: Command.ConflictResolution): T {
    this.conflictResolution = conflictResolution
    return this
}