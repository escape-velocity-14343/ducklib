package com.escapevelocity.ducklib.command.commands

import com.escapevelocity.ducklib.command.commands.group.DeadlineCommandGroup
import com.escapevelocity.ducklib.command.commands.group.ParallelCommandGroup
import com.escapevelocity.ducklib.command.commands.group.RaceCommandGroup
import com.escapevelocity.ducklib.command.commands.group.SequentialCommandGroup

fun Command.alongWith(other: Command): ParallelCommandGroup = ParallelCommandGroup(this, other)
fun Command.andThen(other: Command): SequentialCommandGroup = SequentialCommandGroup(this, other)
fun Command.raceWith(other: Command): RaceCommandGroup = RaceCommandGroup(this, other)
fun Command.deadlineWith(other: Command): DeadlineCommandGroup = DeadlineCommandGroup(this, other)
