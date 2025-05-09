package com.escapevelocity.ducklib.core.command.commands

/**
 * A command that runs [toRun] once the first time the command is executed,
 * and finishes instantly.
 */
class InstantCommand(vararg requirements: Any, val toRun: () -> Unit) : NoOpCommand(requirements) {
    var hasRun = false
    override fun initialize() {
        hasRun = false
    }

    override fun execute() {
        if (hasRun) return
        toRun()
        hasRun = true
    }
}

/**
 * Creates an [InstantCommand] from the given `() -> Unit`, optionally giving it a set of requirements.
 *
 * @param requirements The requirements to require
 */
fun (() -> Unit).instant(vararg requirements: Any) = InstantCommand(requirements, toRun = this)