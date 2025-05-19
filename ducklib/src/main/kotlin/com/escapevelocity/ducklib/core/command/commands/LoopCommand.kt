package com.escapevelocity.ducklib.core.command.commands

/**
 * Calls [loop] forever in [execute].
 * Suspendable by default,
 * however this can be configured with [Command.suspendable].
 *
 * For more customizability, consider [LambdaCommand].
 *
 * [Online documentation](https://escape-velocity-14343.github.io/ducklib/commands/builtin/#loopcommand)
 */
class LoopCommand(vararg requirements: Any, val loop: () -> Unit) : NoOpCommand(requirements) {
    override fun execute() {
        loop()
    }

    override val finished = false
}

fun (() -> Unit).loop(vararg requirements: Any) = LoopCommand(requirements, loop = this)