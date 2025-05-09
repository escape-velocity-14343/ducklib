package com.escapevelocity.ducklib.core.command.commands.composition

import com.escapevelocity.ducklib.core.command.commands.Command

/**
 * Composes [command] to repeat for [times] times.
 *
 * @param times How many times to repeat.
 * For repeating infinite times, set to null (the default).
 */
open class RepeatCommand(val command: Command, val times: Int? = null) : CompositionCommand(command) {
    var repeatTimes = 0
    var initializeNext = false

    override fun initialize() {
        super.initialize()
        repeatTimes = 0
        initializeNext = true
    }

    override fun execute() {
        // just to definitely make sure it doesn't call 'execute' after the command is finished
        if (finished) {
            return
        }

        if (initializeNext) {
            command.initialize()
            initializeNext = false
        }

        super.execute()

        if (command.finished) {
            command.end(false)
            repeatTimes++
        }
    }

    override val finished: Boolean
        get() = times != null && repeatTimes >= times
}

/**
 * Composes this command inside a [RepeatCommand].
 *
 * @param times How many times to repeat.
 * For repeating infinite times, set to null (the default).
 */
fun Command.repeat(times: Int? = null) = RepeatCommand(this, times)