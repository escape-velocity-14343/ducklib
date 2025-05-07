@file:Suppress("SpellCheckingInspection")

package com.escapevelocity.ducklib.core.command.commands

/**
 * A command that exposes the lifetime functions like [initialize] and [execute] as lambdas.
 *
 * Useful for one-off commands,
 * or commands that capture outer variables.
 */
open class LambdaCommand() : Command() {
    constructor(configuration: LambdaCommand.() -> Unit) : this() {
        this.configuration()
    }

    var lmsuspendable: Boolean = super.suspendable
    var lminitialize: () -> Unit = {}
    var lmexecute: () -> Unit = {}
    var lmfinished: () -> Boolean = { false }
    var lmsuspend: () -> Unit = {}
    var lmresume: () -> Unit = {}
    var lmend: (Boolean) -> Unit = {}

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
}