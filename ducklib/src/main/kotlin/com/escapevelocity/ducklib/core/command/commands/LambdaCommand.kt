@file:Suppress("SpellCheckingInspection")

package com.escapevelocity.ducklib.core.command.commands

class LambdaCommand(vararg requirements: Any) : Command() {
    constructor(vararg requirements: Any, configuration: LambdaCommand.() -> Unit) : this(*requirements) {
        this.configuration()
    }

    init {
        addRequirements(requirements)
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

    override fun end(interrupted: Boolean) {
        lmend(interrupted)
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