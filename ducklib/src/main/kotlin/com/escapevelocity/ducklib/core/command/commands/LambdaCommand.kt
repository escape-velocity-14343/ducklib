@file:Suppress("SpellCheckingInspection")

package com.escapevelocity.ducklib.core.command.commands

/**
 * A command that exposes the lifetime functions like [initialize] and [execute] as lambdas.
 *
 * Useful for one-off commands,
 * or commands that capture outer variables to use as state.
 *
 * [Online documentation](https://escape-velocity-14343.github.io/ducklib/commands/builtin/#lambdacommand)
 */
open class LambdaCommand() : Command() {
    constructor(builder: LambdaCommandBuilder.() -> Unit) : this() {
        val lcb = LambdaCommandBuilder()
        lcb.builder()

        // copy over parameters set by the LambdaCommandBuilder
        lminitialize = lcb.initialize
        lmexecute = lcb.execute
        lmfinished = lcb.finished
        lmsuspend = lcb.suspend
        lmresume = lcb.resume
        lmend = lcb.end
        lmsuspendable = lcb.suspendable
        lcb.config(this)
    }

    /**
     * Whether this command is suspendable or not.
     *
     * A suspendable command will get suspended if another conflicting command gets scheduled. As a result, [run] will
     * no longer get called.
     * When the command is suspended [suspend] will be called,
     * and [resume] will be called once when the command is ready to be run again.
     */
    var lmsuspendable: Boolean = super.suspendable

    /**
     * Called once when the command is scheduled. Override to provide custom behavior
     */
    var lminitialize: () -> Unit = {}

    /**
     * Called every time [com.escapevelocity.ducklib.core.command.scheduler.CommandScheduler.run] is called
     * while the command is active
     */
    var lmexecute: () -> Unit = {}

    /**
     * Checked every time [com.escapevelocity.ducklib.core.command.scheduler.CommandScheduler.run] is called,
     * after [Command.execute].
     * Commands are guaranteed to run
     * [Command.execute] once, even if this method returns `true`.
     *
     * By default, commands will only run [execute] once,
     * however, this may vary depending on the specific command.
     */
    var lmfinished: () -> Boolean = { true }

    /**
     * Called when the command scheduler suspends this command.
     *
     * The command scheduler will only suspend a command if [suspendable] is `true`, otherwise if a conflicting command
     * with higher priority gets scheduled this command will get descheduled.
     */
    var lmsuspend: () -> Unit = {}

    /**
     * Called when the command scheduler resumes this command.
     */
    var lmresume: () -> Unit = {}

    /**
     * Called when the command is finished and the command scheduler is about to deschedule the command
     * @param canceled If the command was interrupted,
     * such as by calling [com.escapevelocity.ducklib.core.command.scheduler.CommandScheduler.cancel]
     */
    var lmend: (canceled: Boolean) -> Unit = {}

    override val suspendable: Boolean
        get() = lmsuspendable

    override fun initialize() {
        lminitialize()
    }

    override fun execute() {
        lmexecute()
    }

    override val finished: Boolean
        get() = lmfinished()

    override fun suspend() {
        lmsuspend()
    }

    override fun resume() {
        lmresume()
    }

    override fun end(canceled: Boolean) {
        lmend(canceled)
    }

    fun setSuspendable(suspendable: Boolean) {
        lmsuspendable = suspendable
    }

    fun setInitialize(initialize: () -> Unit) {
        lminitialize = initialize
    }

    fun setExecute(execute: () -> Unit) {
        lmexecute = execute
    }

    fun setFinished(finished: () -> Boolean) {
        lmfinished = finished
    }

    fun setSuspend(suspend: () -> Unit) {
        lmsuspend = suspend
    }

    fun setResume(resume: () -> Unit) {
        lmresume = resume
    }

    fun setEnd(end: (Boolean) -> Unit) {
        lmend = end
    }

    class LambdaCommandBuilder internal constructor() {

        /**
         * Whether this command is suspendable or not.
         *
         * A suspendable command will get suspended if another conflicting command gets scheduled. As a result, [run] will
         * no longer get called.
         * When the command is suspended [suspend] will be called,
         * and [resume] will be called once when the command is ready to be run again.
         */
        var suspendable: Boolean = true

        /**
         * Called once when the command is scheduled. Override to provide custom behavior
         */
        var initialize: () -> Unit = {}

        /**
         * Called every time [com.escapevelocity.ducklib.core.command.scheduler.CommandScheduler.run] is called
         * while the command is active
         */
        var execute: () -> Unit = {}

        /**
         * Checked every time [com.escapevelocity.ducklib.core.command.scheduler.CommandScheduler.run] is called,
         * after [Command.execute].
         * Commands are guaranteed to run
         * [Command.execute] once, even if this method returns `true`.
         *
         * By default, commands will only run [execute] once,
         * however, this may vary depending on the specific command.
         */
        var finished: () -> Boolean = { true }

        /**
         * Called when the command scheduler suspends this command.
         *
         * The command scheduler will only suspend a command if [suspendable] is `true`, otherwise if a conflicting command
         * with higher priority gets scheduled this command will get descheduled.
         */
        var suspend: () -> Unit = {}

        /**
         * Called when the command scheduler resumes this command.
         */
        var resume: () -> Unit = {}

        /**
         * Called when the command is finished and the command scheduler is about to deschedule the command
         * @param canceled If the command was interrupted,
         * such as by calling [com.escapevelocity.ducklib.core.command.scheduler.CommandScheduler.cancel]
         */
        var end: (canceled: Boolean) -> Unit = {}

        var config: LambdaCommand.() -> Unit = {}
    }
}