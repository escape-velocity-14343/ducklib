package com.escapevelocity.ducklib.core.command.commands

import com.escapevelocity.ducklib.core.util.b16Hash

abstract class Command {

    private val _requirements: MutableSet<Any> = HashSet()
    val requirements: Set<Any>
        get() = _requirements
    var inGroup = false
        internal set

    open var name: String = javaClass.simpleName

    /**
     * Sets which action to take when the conflicting command has higher priority to this one.
     */
    var onHigherConflict = OnHigherConflict.QUEUE

    /**
     * Sets which action to take when the conflicting command has equal priority to this one.
     */
    var onEqualConflict = OnEqualConflict.OVERRIDE

    /**
     * The priority of this command. Higher-priority commands will be scheduled over lower-priority ones.
     */
    var priority = Priority.LOWEST

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
}

/**
 * An enum that represents what should happen when the conflicting command has a higher priority than this command
 *
 * **NOTE**:
 * A command that gets scheduled that has a higher priority than all other conflicting commands *will
 * always get scheduled* no matter what.
 */
enum class OnHigherConflict {
    /**
     * The command won't be attempted to be scheduled later at all.
     */
    CANCEL,

    /**
     * When a conflict happens,
     * the command will be moved to the command queue to be scheduled or rescheduled later.
     *
     * This can be used to implement input buffering in TeleOps
     * where if one command is currently running
     * and another is running with equal priority,
     * it will queue until the other is finished.
     */
    QUEUE,
}

/**
 * An enum that represents what should happen when the conflicting command has an equal priority to this command
 *
 * **NOTE**:
 * A command that gets scheduled that has a higher priority than all other conflicting commands *will always get
 * scheduled* no matter what.
 */
enum class OnEqualConflict {
    /**
     * The conflicting command will be overridden
     */
    OVERRIDE,

    /**
     * When a conflict happens,
     * the command will be moved to the command queue to be scheduled or rescheduled later
     */
    QUEUE,
}

/**
 * Sets the [name] of the [Command].
 *
 * **NOTE**: If you need to set multiple methods, consider [configure] instead
 * @return The command with the same type to facilitate chaining
 */
fun <T : Command> T.setName(name: String) = configure { this.name = name }

/**
 * Sets the [priority] of the [Command].
 *
 * **NOTE**: If you need to set multiple methods, consider [configure] instead
 * @return The command with the same type to facilitate chaining
 */
fun <T : Command> T.setPriority(priority: Priority) = configure { this.priority = priority }

/**
 * Sets the [Command.onHigherConflict] of the [Command].
 *
 * **NOTE**: If you need to set multiple methods, consider [configure] instead
 * @return The command with the same type to facilitate chaining
 */
fun <T : Command> T.setOnHigherConflict(onHigherConflict: OnHigherConflict) =
    configure { this.onHigherConflict = onHigherConflict }

/**
 * Sets the [Command.onEqualConflict] of the [Command].
 *
 * **NOTE**: If you need to set multiple methods, consider [configure] instead
 *
 * @return The command with the same type to facilitate chaining
 */
fun <T : Command> T.setOnEqualConflict(onEqualConflict: OnEqualConflict) =
    configure { this.onEqualConflict = onEqualConflict }

/**
 * Configures a command instance using the specified builder function and returns the configured instance.
 *
 * @param configuration A function used to configure this command instance.
 * @return The configured command instance.
 * @sample com.escapevelocity.ducklib.core.samples.inlineCommandConfigurationSample
 * @sample com.escapevelocity.ducklib.core.samples.statementCommandConfigurationSample
 */
inline fun <T : Command> T.configure(configuration: T.() -> Unit): T {
    this.configuration()
    return this
}