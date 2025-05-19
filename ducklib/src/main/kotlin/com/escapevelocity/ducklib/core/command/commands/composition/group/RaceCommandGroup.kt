package com.escapevelocity.ducklib.core.command.commands.composition.group

import com.escapevelocity.ducklib.core.command.commands.Command

/**
 * Runs the given commands in parallel and finishes when one command finishes.
 *
 * Commands *can* share requirements, unlike [ParallelCommandGroup]
 *
 * [Online documentation](https://escape-velocity-14343.github.io/ducklib/commands/groups/#racecommandgroup)
 * @param commands The commands to run in parallel
 * @sample [com.escapevelocity.ducklib.core.samples.raceCommandGroupSample]
 */
open class RaceCommandGroup(vararg commands: Command) : ParallelCommandGroup(*commands) {
    override val finished
        get() = _commands!!.values.any { it }
}