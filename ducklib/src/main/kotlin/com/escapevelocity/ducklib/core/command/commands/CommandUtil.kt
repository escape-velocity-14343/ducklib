package com.escapevelocity.ducklib.core.command.commands

import com.escapevelocity.ducklib.core.command.commands.group.DeadlineCommandGroup
import com.escapevelocity.ducklib.core.command.commands.group.ParallelCommandGroup
import com.escapevelocity.ducklib.core.command.commands.group.RaceCommandGroup
import com.escapevelocity.ducklib.core.command.commands.group.SequentialCommandGroup

infix fun Command.raceWith(right: Command): RaceCommandGroup = RaceCommandGroup(this, right)
infix fun Command.deadlineWith(right: Command): DeadlineCommandGroup = DeadlineCommandGroup(right, this)

infix fun Command.with(right: Command): ParallelCommandGroup {
    if (this is ParallelCommandGroup) {
        this.addCommands(right)
        return this
    }

    return ParallelCommandGroup(this, right)
}

fun Command.with(vararg right: Command): ParallelCommandGroup {
    if (this is ParallelCommandGroup) {
        this.addCommands(*right)
        return this
    }

    return ParallelCommandGroup(this, *right)
}

infix fun Command.then(right: Command): SequentialCommandGroup {
    if (this is SequentialCommandGroup) {
        this.addCommands(right)
        return this
    }

    return SequentialCommandGroup(this, right)
}

fun Command.then(vararg right: Command): SequentialCommandGroup {
    if (this is SequentialCommandGroup) {
        this.addCommands(*right)
        return this
    }

    return SequentialCommandGroup(this, *right)
}
