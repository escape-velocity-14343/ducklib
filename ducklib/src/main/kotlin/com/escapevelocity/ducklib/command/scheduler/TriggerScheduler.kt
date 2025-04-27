package com.escapevelocity.ducklib.command.scheduler

import com.escapevelocity.ducklib.command.trigger.Trigger

interface TriggerScheduler {
    /**
     * Bind a boolean supplier to an action. When [trigger] is true, the [action] will be run by the [TriggerScheduler]
     */
    fun bind(trigger: () -> Boolean, originalTrigger: Trigger? = null, action: () -> Unit)

    /**
     * Run the [TriggerScheduler]
     */
    fun run()

    /**
     * Reset the state of the scheduler
     */
    fun reset()
}