package com.escapevelocity.ducklib.command.trigger

import com.escapevelocity.ducklib.command.commands.Command
import com.escapevelocity.ducklib.command.scheduler.CommandScheduler
import com.escapevelocity.ducklib.command.scheduler.TriggerScheduler

open class Trigger(private val ts: TriggerScheduler, private val cs: CommandScheduler, private val trigger: () -> Boolean): () -> Boolean {
    override fun invoke(): Boolean = trigger.invoke()

    /**
     * Schedules [command] when the trigger moves from false to true
     * @param command The command to schedule
     */
    fun onceOnTrue(command: Command): Trigger {
        var lastVal = this()
        ts.bind({
            val thisVal = this()
            val ret = thisVal && !lastVal
            lastVal = thisVal
            ret
        }, {
            with (cs) {
                command.schedule()
            }
        })
        return this
    }

    /**
     * Schedules [command] when the trigger moves from true to false
     * @param command The command to schedule
     */
    fun onceOnFalse(command: Command): Trigger {
        (!this).onceOnTrue(command)
        return this
    }

    /**
     * Schedules [command] continuously while the trigger is true
     * @param command The command to schedule
     */
    fun whileOnTrue(command: Command): Trigger {
        ts.bind(this) { cs.scheduleCommand(command) }
        return this
    }

    /**
     * Schedules [command] continuously while the trigger is true
     * @param command The command to schedule
     */
    fun <T: Trigger> whileOnFalse(command: Command): Trigger {
        ts.bind(!this) { cs.scheduleCommand(command) }
        return this
    }

    operator fun not(): Trigger = Trigger(ts, cs) { !this() }

    infix fun and(other: () -> Boolean) = Trigger(ts, cs) { this() && other() }

    infix fun or(other: () -> Boolean) = Trigger(ts, cs) { this() || other() }

    infix fun xor(other: () -> Boolean) = Trigger(ts, cs) { this() xor other() }
}
