package com.escapevelocity.ducklib.core.command.commands

/**
 * Defers command construction until scheduling time.
 * This is useful when you have dynamically changing parameters,
 * but you don't want every parameter to use a `() -> T` type.
 */
class DeferredCommand(vararg requirements: Any, val commandSupplier: () -> Command) : Command() {
    init {
        addRequirements(requirements)
    }

    private var cmd: Command? = null

    override val suspendable: Boolean
        get() = cmd?.suspendable ?: false

    override fun initialize() {
        cmd = commandSupplier()
        cmd?.initialize()
    }

    override fun execute() {
        cmd?.execute()
    }

    override val finished: Boolean
        get() = cmd?.finished == true

    override fun suspend() {
        cmd?.suspend()
    }

    override fun resume() {
        cmd?.resume()
    }

    override fun end(interrupted: Boolean) {
        cmd?.end(interrupted)
    }
}

fun (() -> Command).deferred(vararg requirements: Any) = DeferredCommand(*requirements, commandSupplier = this)