package com.escapevelocity.ducklib.command.scheduler

import com.escapevelocity.ducklib.command.commands.Command
import com.escapevelocity.ducklib.command.commands.Command.SubsystemConflictResolution
import com.escapevelocity.ducklib.command.subsystem.Subsystem
import util.containsAny

/**
 * An interface that defines a command scheduler - a class that allows you to add commands, run added commands, and
 * cancel those commands
 */
interface CommandScheduler {
    fun schedule(command: Command)

    fun cancel(command: Command)

    fun run()

    fun Command.scheduleCommand() {
        schedule(this)
    }

    fun Command.cancelCommand() {
        cancel(this)
    }
}