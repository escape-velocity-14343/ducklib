package com.escapevelocity.ducklib.core.command.scheduler

interface TriggerScheduler {
    /**
     * Bind a boolean supplier to an action. When [trigger] is true, the [action] will be run by the [TriggerScheduler]
     */
    fun bind(trigger: () -> Boolean, originalTrigger: (() -> Boolean)? = null, action: () -> Unit)

    /**
     * Run the [TriggerScheduler]
     */
    fun run()

    /**
     * Reset the state of the scheduler
     */
    fun reset()

    /**
     * Runs [action] when the trigger moves from false to true
     * @param action The action to run
     * @sample com.escapevelocity.ducklib.core.samples.triggerOnceOnSample
     */
    fun <T : () -> Boolean> T.onceOnTrue(action: () -> Unit): T {
        var lastVal = this()
        bind({
            val thisVal = this()
            val ret = thisVal && !lastVal
            lastVal = thisVal
            ret
        }, this, action)
        return this
    }

    fun <T : () -> Boolean> T.onceOnFalse(action: () -> Unit): T {
        var lastVal = this()
        bind({
            val thisVal = this()
            val ret = !thisVal && lastVal
            lastVal = thisVal
            ret
        }, this, action)
        return this
    }

    fun <T : () -> Boolean> T.whileOnTrue(risingAction: () -> Unit, fallingAction: () -> Unit) =
        onceOnTrue(risingAction).onceOnFalse(fallingAction)

    fun <T : () -> Boolean> T.whileOnFalse(risingAction: () -> Unit, fallingAction: () -> Unit) =
        onceOnTrue(fallingAction).onceOnFalse(risingAction)
}