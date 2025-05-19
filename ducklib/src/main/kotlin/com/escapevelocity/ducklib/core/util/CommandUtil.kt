package com.escapevelocity.ducklib.core.util

import com.escapevelocity.ducklib.core.command.commands.Command
import com.escapevelocity.ducklib.core.command.commands.composition.TimeoutCommand
import com.escapevelocity.ducklib.core.command.commands.composition.group.*
import com.escapevelocity.ducklib.core.command.commands.configure
import kotlin.time.Duration

/**
 * Construct a [RaceCommandGroup].
 * If you want to group multiple commands in the group,
 * chain `with` (e.g. `a with b with c`), use [and], or use the non-infix operator that accepts varargs.
 *
 * [Online documentation](https://escape-velocity-14343.github.io/ducklib/commands/groups/#infix-operators)
 * @param right The command to race with
 * @sample com.escapevelocity.ducklib.core.samples.raceCommandGroupSample
 */
infix fun Command.races(right: Command) = grouped(right) { RaceCommandGroup(this, right) }

/**
 * Construct a [RaceCommandGroup].
 * If you want to group multiple commands in the group,
 * chain `with` (e.g. `a with b with c`), use [and], or use the non-infix operator that accepts varargs.
 *
 * [Online documentation](https://escape-velocity-14343.github.io/ducklib/commands/groups/#infix-operators)
 * @param right The command(s) to race with
 * @sample com.escapevelocity.ducklib.core.samples.raceCommandGroupSample
 */
fun Command.races(vararg right: Command) = grouped(*right) { RaceCommandGroup(this, *right) }

/**
 * Construct a [DeadlineCommandGroup].
 * Here, `this` is the deadline and [right] is the rest of the group.
 * If you want to group multiple commands in the deadline group,
 * use [and] or the non-infix operator that accepts varargs.
 *
 * [Online documentation](https://escape-velocity-14343.github.io/ducklib/commands/groups/#infix-operators)
 * @param right The command(s) to race with
 * @sample com.escapevelocity.ducklib.core.samples.deadlineCommandGroupSample
 */
infix fun Command.deadlines(right: Command) = DeadlineCommandGroup(this, right)

/**
 * Construct a [DeadlineCommandGroup].
 * Here, `this` is the deadline and [right] is the rest of the group.
 * If you want to group multiple commands in the deadline group, use [and].
 *
 * [Online documentation](https://escape-velocity-14343.github.io/ducklib/commands/groups/#infix-operators)
 * @param right The command(s) to race with
 * @sample com.escapevelocity.ducklib.core.samples.deadlineCommandGroupSample
 */
fun Command.deadlines(vararg right: Command) = DeadlineCommandGroup(this, *right)

/**
 * Construct a [ParallelCommandGroup].
 * If you want to group multiple commands in the group,
 * chain `with` calls (e.g. `a with b with c`), use [and], or use the non-infix operator that accepts varargs.
 *
 * **NOTE**:
 * If the receiver command is a ParallelCommandGroup,
 * this will add it to the existing command group instead of composing it inside a new command group.
 *
 * [Online documentation](https://escape-velocity-14343.github.io/ducklib/commands/groups/#infix-operators)
 * @param right The command to parallel with
 * @sample com.escapevelocity.ducklib.core.samples.raceCommandGroupSample
 */
infix fun Command.with(right: Command) =
    grouped(right) { ParallelCommandGroup(this, right) }

/**
 * Construct a [ParallelCommandGroup].
 *
 * **NOTE**:
 * If the receiver command is a ParallelCommandGroup,
 * this will add it to the existing command group instead of composing it inside a new command group.
 *
 * [Online documentation](https://escape-velocity-14343.github.io/ducklib/commands/groups/#infix-operators)
 * @param right The command to parallel with
 * @sample com.escapevelocity.ducklib.core.samples.raceCommandGroupSample
 */
fun Command.with(vararg right: Command) =
    grouped(*right) { ParallelCommandGroup(this, *right) }

/**
 * Construct a [SequentialCommandGroup].
 * If you want to group multiple commands in the group,
 * chain `then` calls (e.g. `a with b with c`), use [and], or use the non-infix operator that accepts varargs.
 *
 * **NOTE**:
 * If the receiver command is a [SequentialCommandGroup],
 * this will add it to the existing command group instead of composing it inside a new command group.
 *
 * [Online documentation](https://escape-velocity-14343.github.io/ducklib/commands/groups/#infix-operators)
 * @param right The command to sequential with
 * @sample com.escapevelocity.ducklib.core.samples.raceCommandGroupSample
 */
infix fun Command.then(right: Command) =
    grouped(right) { SequentialCommandGroup(this, right) }

/**
 * Construct a [SequentialCommandGroup].
 * If you want to group multiple commands in the group,
 * chain `then` calls (e.g. `a with b with c`), use [and], or use the non-infix operator that accepts varargs.
 *
 * **NOTE**:
 * If the receiver command is a [SequentialCommandGroup],
 * this will add it to the existing command group instead of composing it inside a new command group.
 *
 * [Online documentation](https://escape-velocity-14343.github.io/ducklib/commands/groups/#infix-operators)
 * @param right The command to sequential with
 * @sample com.escapevelocity.ducklib.core.samples.raceCommandGroupSample
 */
fun Command.then(vararg right: Command) =
    grouped(*right) { SequentialCommandGroup(this, *right) }

/**
 * Adds a set of commands to a [CommandGroup].
 *
 * [Online documentation](https://escape-velocity-14343.github.io/ducklib/commands/groups/#infix-operators)
 * @param right The command to append to the group
 */
infix fun CommandGroup.and(right: Command) = configure { addCommands(right) }

/**
 * Adds a set of commands to a [CommandGroup].
 *
 * [Online documentation](https://escape-velocity-14343.github.io/ducklib/commands/groups/#infix-operators)
 * @param right The commands to append to the group
 */
fun CommandGroup.and(vararg right: Command) = configure { addCommands(*right) }

/**
 * Little utility that groups a command with another group.
 * If the other group is already a CommandGroup, it'll just add the commands to the group,
 * otherwise it'll use [constructor] to create a new group instance.
 */
private inline fun <reified T : CommandGroup> Command.grouped(vararg with: Command, constructor: () -> T) =
    when (this) {
        // need special check
        // because even though RaceCommandGroup is a subtype of ParallelCommandGroup,
        // that doesn't mean
        // it should just add the command to the RaceCommandGroup instead of composing a new ParallelCommandGroup
        is T if this::class == T::class -> configure { addCommands(*with) }
        else -> constructor()
    }

/**
 * Adds a timeout to a given command by composing it inside a [TimeoutCommand].
 *
 * @param duration How long the timeout will last
 * @param trackSuspensionTime Whether the [TimeoutCommand] will subtract from the total time the amount of time
 * suspended
 */
fun Command.withTimeout(duration: Duration, trackSuspensionTime: Boolean = true) =
    TimeoutCommand(this, duration, trackSuspensionTime)