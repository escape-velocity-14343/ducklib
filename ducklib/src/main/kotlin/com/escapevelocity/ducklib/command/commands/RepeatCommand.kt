package com.escapevelocity.ducklib.command.commands

class RepeatCommand(val command: Command, val times: Int? = null) : Command() {
    override val suspendable: Boolean
        get() = command.suspendable

    var repeatTimes = 0

    override fun initialize() {
        command.initialize()
        repeatTimes = 0
    }

    override fun execute() {
        // just to definitely make sure it doesn't call 'execute' after the command is finished
        if (finished) {
            return
        }

        command.execute()

        if (command.finished) {
            command.initialize()
            repeatTimes++
        }
    }

    override val finished: Boolean
        get() = times != null && repeatTimes >= times

    override fun suspend() {
        command.suspend()
    }

    override fun resume() {
        command.resume()
    }

    override fun end(interrupted: Boolean) {
        command.end(interrupted)
    }
}

fun Command.repeat(times: Int? = null) = RepeatCommand(this, times)