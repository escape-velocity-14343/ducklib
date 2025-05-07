package com.escapevelocity.ducklib.core.command.scheduler

/**
 * An interface that defines a trigger scheduler -
 * a class that allows you to bind `() -> Boolean`s to `() -> Unit`s,
 * while also handling rising-edge and falling-edge logic.
 */
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
     * Run [action] on the rising edge of the boolean supplier.
     *
     * @param action The action to run
     * @return The same boolean supplier for chaining calls
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

    /**
     * Run [action] on the falling edge of the boolean supplier.
     *
     * @param action The action to run
     * @return The same boolean supplier for chaining calls
     * @sample com.escapevelocity.ducklib.core.samples.triggerOnceOnSample
     */
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

    /**
     * Run [risingAction] on the rising edge of the boolean supplier and [fallingAction] on the falling edge.
     *
     * @param risingAction The action to run on the rising edge
     * @param fallingAction The action to run on the falling edge
     * @return The same boolean supplier for chaining calls
     * @sample com.escapevelocity.ducklib.core.samples.triggerOnceOnSample
     */
    fun <T : () -> Boolean> T.whileOnTrue(risingAction: () -> Unit, fallingAction: () -> Unit) =
        onceOnTrue(risingAction).onceOnFalse(fallingAction)
}