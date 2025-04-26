package com.escapevelocity.ducklib.command.commands

import com.escapevelocity.ducklib.command.commands.group.DeadlineCommandGroup
import com.escapevelocity.ducklib.command.commands.group.ParallelCommandGroup
import com.escapevelocity.ducklib.command.commands.group.RaceCommandGroup
import com.escapevelocity.ducklib.command.commands.group.SequentialCommandGroup

infix fun Command.raceWith(other: Command): RaceCommandGroup = RaceCommandGroup(this, other)
infix fun Command.deadlineWith(other: Command): DeadlineCommandGroup = DeadlineCommandGroup(this, other)

infix fun Command.with(other: Command): ParallelCommandGroup {
    if (this is ParallelCommandGroup) {
        this.addCommands(other)
        return this
    }

    return ParallelCommandGroup(this, other)
}

fun Command.with(vararg other: Command): ParallelCommandGroup {
    if (this is ParallelCommandGroup) {
        this.addCommands(*other)
        return this
    }

    return ParallelCommandGroup(this, *other)
}

infix fun Command.then(other: Command): SequentialCommandGroup {
    if (this is SequentialCommandGroup) {
        this.addCommands(other)
        return this
    }

    return SequentialCommandGroup(this, other)
}

fun Command.then(vararg other: Command): SequentialCommandGroup {
    if (this is SequentialCommandGroup) {
        this.addCommands(*other)
        return this
    }

    return SequentialCommandGroup(this, *other)
}
