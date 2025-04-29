package com.escapevelocity.ducklib.core.command.commands.group

import com.escapevelocity.ducklib.core.command.commands.Command

class RaceCommandGroup(vararg commands: Command): ParallelCommandGroup(*commands) {
    override val finished
        get() = _commands!!.values.any { it }
}