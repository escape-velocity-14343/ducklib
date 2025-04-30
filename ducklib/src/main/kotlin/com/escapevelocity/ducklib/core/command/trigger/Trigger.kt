package com.escapevelocity.ducklib.core.command.trigger

import com.escapevelocity.ducklib.core.command.commands.Command
import com.escapevelocity.ducklib.core.command.scheduler.CommandScheduler
import com.escapevelocity.ducklib.core.command.scheduler.TriggerScheduler

open class Trigger(
    private val ts: TriggerScheduler,
    private val cs: CommandScheduler,
    private val trigger: () -> Boolean
) : () -> Boolean {
    override fun invoke(): Boolean = trigger.invoke()

    /**
     * Schedules [command] when the trigger moves from false to true
     * @param command The command to schedule
     * @sample com.escapevelocity.ducklib.core.samples.triggerOnceOnSample
     */
    fun onceOnTrue(command: Command) = onceOnTrue { cs.scheduleCommand(command) }

    /**
     * Runs [action] when the trigger moves from false to true
     * @param action The action to run
     * @sample com.escapevelocity.ducklib.core.samples.triggerOnceOnSample
     */
    fun onceOnTrue(action: () -> Unit): Trigger {
        var lastVal = this()
        ts.bind({
            val thisVal = this()
            val ret = thisVal && !lastVal
            lastVal = thisVal
            ret
        }, this, action)
        return this
    }

    /**
     * Schedules [command] when the trigger moves from false to true and cancels it when it moves to false
     *
     * **NOTE**: This works by binding two triggers: one that schedules the command when the trigger moves from false to
     * true, and one that cancels it when it moves from true to false. Don't be surprised when you see twice as many
     * bound triggers as you expect.
     * @param command The command to schedule
     * @sample com.escapevelocity.ducklib.core.samples.triggerWhileOnSample
     */
    fun whileOnTrue(command: Command) = whileOnTrue({ cs.scheduleCommand(command) }, { cs.cancelCommand(command) })

    /**
     * Runs [onTrueAction] when the trigger moves from false to true, and [onFalseAction] when the trigger moves from
     * true to false
     *
     * **NOTE**: This works by binding two triggers: one that schedules the command when the trigger moves from false to
     * true, and one that cancels it when it moves from true to false. Don't be surprised when you see twice as many
     * bound triggers as you expect.
     * @param onTrueAction The action to run when the trigger moves from false to true
     * @param onTrueAction The action to run when the trigger moves from true to false
     * @sample com.escapevelocity.ducklib.core.samples.triggerWhileOnSample
     */
    fun whileOnTrue(onTrueAction: () -> Unit, onFalseAction: () -> Unit): Trigger {
        var lastValT = this()
        ts.bind({
            val thisVal = this()
            val ret = thisVal && !lastValT
            lastValT = thisVal
            ret
        }, this, onTrueAction)

        var lastValF = this()
        ts.bind({
            val thisVal = this()
            val ret = !thisVal && lastValF
            lastValF = thisVal
            ret
        }, this, onFalseAction)
        return this
    }

    /**
     * Schedules [command] when the trigger moves from true to false
     * @param command The command to schedule
     * @sample com.escapevelocity.ducklib.core.samples.triggerOnceOnSample
     */
    fun onceOnFalse(command: Command): Trigger {
        (!this).onceOnTrue(command)
        return this
    }

    /**
     * Runs [action] when the trigger moves from true to false
     * @param action The action to run
     * @sample com.escapevelocity.ducklib.core.samples.triggerOnceOnSample
     */
    fun onceOnFalse(action: () -> Unit): Trigger {
        (!this).onceOnTrue(action)
        return this
    }

    /**
     * Schedules [command] when the trigger moves from true to false and cancels it when it moves to true
     *
     * **NOTE**: This works by binding two triggers: one that schedules the command when the trigger moves from false to
     * true, and one that cancels it when it moves from true to false. Don't be surprised when you see twice as many
     * bound triggers as you expect.
     * @param command The command to schedule
     * @sample com.escapevelocity.ducklib.core.samples.triggerWhileOnSample
     */
    fun whileOnFalse(command: Command): Trigger {
        (!this).whileOnTrue(command)
        return this
    }

    /**
     * Creates a new trigger that will be false when this is true, and true when this is false
     * @sample com.escapevelocity.ducklib.core.samples.triggerInversionSample
     */
    operator fun not(): Trigger = Trigger(ts, cs) { !this() }

    /**
     * Composes this trigger with [other] to create a new trigger that will only trigger when both triggers are true.
     * @param other The other trigger
     * @sample com.escapevelocity.ducklib.core.samples.triggerCombinationSample
     */
    infix fun and(other: () -> Boolean) = Trigger(ts, cs) { this() && other() }

    /**
     * Composes this trigger with [other] to create a new trigger that will only trigger when either trigger is true.
     * @param other The other trigger
     * @sample com.escapevelocity.ducklib.core.samples.triggerCombinationSample
     */
    infix fun or(other: () -> Boolean) = Trigger(ts, cs) { this() || other() }

    /**
     * Composes this trigger with [other] to create a new trigger that will only trigger when one or the other trigger
     * is true, but not both.
     * @param other The other trigger
     * @sample com.escapevelocity.ducklib.core.samples.triggerCombinationSample
     */
    infix fun xor(other: () -> Boolean) = Trigger(ts, cs) { this() xor other() }

    override fun toString() = "${super.toString()} (${if (trigger()) "x" else " "})"
}
