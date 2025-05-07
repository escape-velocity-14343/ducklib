package com.escapevelocity.ducklib.core.command.commands.composition.group

import com.escapevelocity.ducklib.core.command.commands.Command

/**
 * Runs the given commands in parallel and finishes when the "deadline" command finishes,
 * interrupting the rest of the commands.
 *
 * @param deadlineCommand The deadline command
 * @param commands The commands to run in parallel
 * @sample [com.escapevelocity.ducklib.core.samples.deadlineCommandGroupSample]
 */
open class DeadlineCommandGroup(val deadlineCommand: Command, vararg commands: Command) :
    ParallelCommandGroup(deadlineCommand, *commands) {
    override val finished
        get() = deadlineCommand.finished

    override fun Command.prefix() = if (this == deadlineCommand) "#" else " "
}