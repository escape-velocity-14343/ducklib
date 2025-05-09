package com.escapevelocity.ducklib.core.command.commands.composition

import com.escapevelocity.ducklib.core.command.commands.Command
import com.escapevelocity.ducklib.core.command.commands.NoOpCommand

/**
 * Defers command construction until scheduling time.
 * This is useful when you have dynamically changing parameters,
 * but you don't want every parameter to use a `() -> T` type.
 *
 * @param requirements The requirements of the command.
 * This should be the same as the requirements of the command [commandSupplier] will produce.
 * @param commandSupplier The command supplier to be evaluated at initialization time.
 */
open class DeferredCommand(vararg requirements: Any, val commandSupplier: () -> Command) :
    NoOpCommand(requirements) {
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

    override fun end(canceled: Boolean) {
        cmd?.end(canceled)
    }
}

fun (() -> Command).deferred(vararg requirements: Any) = DeferredCommand(*requirements, commandSupplier = this)