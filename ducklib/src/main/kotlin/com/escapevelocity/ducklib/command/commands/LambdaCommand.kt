package com.escapevelocity.ducklib.command.commands

class LambdaCommand : Command() {
    var _suspendable: Boolean = super.suspendable
    var _initialize: () -> Unit = {}
    var _execute: () -> Unit = {}
    var _finished: () -> Boolean = { false }
    var _suspend: () -> Unit = {}
    var _resume: () -> Unit = {}
    var _end: (Boolean) -> Unit = {}

    override val suspendable: Boolean
        get() = _suspendable

    override fun initialize() {
        _initialize()
    }

    override fun execute() {
        _execute()
    }

    override val finished: Boolean
        get() = _finished()

    override fun suspend() {
        _suspend()
    }

    override fun resume() {
        _resume()
    }

    override fun end(interrupted: Boolean) {
        _end(interrupted)
    }

    fun setSuspendable(suspendable: Boolean) {
        _suspendable = suspendable
    }

    fun setInitialize(initialize: () -> Unit) {
        _initialize = initialize
    }

    fun setExecute(execute: () -> Unit) {
        _execute = execute
    }

    fun setFinished(finished: () -> Boolean) {
        _finished = finished
    }

    fun setSuspend(suspend: () -> Unit) {
        _suspend = suspend
    }

    fun setResume(resume: () -> Unit) {
        _resume = resume
    }

    fun setEnd(end: (Boolean) -> Unit) {
        _end = end
    }
}