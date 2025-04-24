package com.escapevelocity.ducklib.command.commands.group

import com.escapevelocity.ducklib.command.commands.Command

class RaceCommandGroup(vararg commands: Command): ParallelCommandGroup(*commands) {
    override val finished = _commands!!.values.any { it }
}