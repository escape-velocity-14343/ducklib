package com.escapevelocity.ducklib.core.command.commands.composition

import com.escapevelocity.ducklib.core.command.commands.Command

/**
 * Composes [command] to repeat for [times] times.
 *
 * [Online documentation](https://escape-velocity-14343.github.io/ducklib/commands/builtin/#repeatcommand)
 * @param times How many times to repeat.
 * For repeating infinite times, set to null (the default).
 */
open class RepeatCommand(val command: Command, val times: Int? = null) : CompositionCommand(command) {
    var repeatTimes = 0
    var initializeNext = false

    override fun initialize() {
        super.initialize()
        repeatTimes = 0
        initializeNext = false
    }

    override fun execute() { // just to definitely make sure it doesn't call 'execute' after the command is finished
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
            initializeNext = true
        }
    }

    override val finished: Boolean
        get() = times != null && repeatTimes >= times
}

/**
 * Composes this command inside a [RepeatCommand].
 *
 * @param times How many times to repeat.
 */
fun Command.repeat(times: Int) = RepeatCommand(this, times)

/**
 * Composes this command inside a [RepeatCommand] that runs forever.
 */
val Command.forever
    get() = RepeatCommand(this)