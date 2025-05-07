package com.escapevelocity.ducklib.core.command.commands.composition

import com.escapevelocity.ducklib.core.command.commands.Command
import com.escapevelocity.ducklib.core.command.commands.WaitCommand
import kotlin.time.Duration

/**
 * Runs the composed command and ends if it times out, or the command finishes.
 *
 * @param command The command to compose
 * @param duration How long it will wait for
 * @param trackSuspensionTime Whether the [TimeoutCommand] will subtract from the total time the amount of time
 * suspended
 */
open class TimeoutCommand(
    val command: Command, duration: Duration, trackSuspensionTime: Boolean = true
) : WaitCommand(duration, trackSuspensionTime) {
    init {
        addRequirements(command)
    }

    private var commandFinished = false

    override val suspendable: Boolean = super.suspendable && command.suspendable

    override fun initialize() {
        super.initialize()
        command.initialize()
        commandFinished = false
    }

    override fun execute() {
        command.execute()
        commandFinished = commandFinished || command.finished
    }

    override fun end(canceled: Boolean) {
        super.end(canceled)
        command.end(canceled || !commandFinished)
    }

    override fun suspend() {
        super.suspend()
        command.suspend()
    }

    override fun resume() {
        super.resume()
        command.resume()
    }

    override val finished
        get() = super.finished || commandFinished

    override fun toString(): String = super.toString() + " [$command]"
}