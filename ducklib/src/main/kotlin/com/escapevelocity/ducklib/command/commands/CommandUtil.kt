package com.escapevelocity.ducklib.command.commands

import com.escapevelocity.ducklib.command.commands.group.DeadlineCommandGroup
import com.escapevelocity.ducklib.command.commands.group.ParallelCommandGroup
import com.escapevelocity.ducklib.command.commands.group.RaceCommandGroup
import com.escapevelocity.ducklib.command.commands.group.SequentialCommandGroup

infix fun Command.alongWith(other: Command): ParallelCommandGroup = ParallelCommandGroup(this, other)
infix fun Command.andThen(other: Command): SequentialCommandGroup = SequentialCommandGroup(this, other)
infix fun Command.raceWith(other: Command): RaceCommandGroup = RaceCommandGroup(this, other)
infix fun Command.deadlineWith(other: Command): DeadlineCommandGroup = DeadlineCommandGroup(this, other)

infix fun Command.with(other: Command): ParallelCommandGroup = this alongWith other
infix fun Command.then(other: Command): SequentialCommandGroup = this andThen other