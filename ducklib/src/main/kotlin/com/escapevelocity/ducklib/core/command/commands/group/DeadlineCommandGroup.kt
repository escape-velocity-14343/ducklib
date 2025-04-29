package com.escapevelocity.ducklib.core.command.commands.group

import com.escapevelocity.ducklib.core.command.commands.Command
import java.security.InvalidParameterException

class DeadlineCommandGroup(private var deadlineCommand: Command, vararg commands: Command): ParallelCommandGroup(deadlineCommand, *commands) {
    fun setDeadline(deadlineCommand: Command): DeadlineCommandGroup {
        if (deadlineCommand !in commands) {
            throw InvalidParameterException("A deadline command must be also scheduled from in the group")
        }
        this.deadlineCommand = deadlineCommand
        return this
    }

    override val finished
        get() = deadlineCommand.finished

    override fun Command.prefix() = if (this == deadlineCommand) "#" else " "
}