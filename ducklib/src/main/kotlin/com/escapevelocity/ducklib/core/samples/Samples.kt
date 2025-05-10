package com.escapevelocity.ducklib.core.samples

import com.escapevelocity.ducklib.core.command.commands.*
import com.escapevelocity.ducklib.core.command.commands.composition.WhenCommand
import com.escapevelocity.ducklib.core.command.commands.composition.group.DeadlineCommandGroup
import com.escapevelocity.ducklib.core.command.commands.composition.group.ParallelCommandGroup
import com.escapevelocity.ducklib.core.command.commands.composition.group.RaceCommandGroup
import com.escapevelocity.ducklib.core.command.commands.composition.group.SequentialCommandGroup
import com.escapevelocity.ducklib.core.command.scheduler.CommandScheduler
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler.Companion.onceOnTrue
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler.Companion.schedule
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler.Companion.whileOnTrue
import com.escapevelocity.ducklib.core.util.*
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

fun inlineCommandConfigurationSample() {
    val cmd = WaitCommand(5.seconds).configure {
        priority = Priority.LOWEST
        name = "MyWaitCommand"
        onHigherConflict = OnHigherConflict.CANCEL
        onEqualConflict = OnEqualConflict.QUEUE
    }
}

fun statementCommandConfigurationSample() {
    val cmd = WaitCommand(5.seconds)
    cmd.configure {
        priority = Priority.LOWEST
        name = "MyWaitCommand"
        onHigherConflict = OnHigherConflict.CANCEL
        onEqualConflict = OnEqualConflict.QUEUE
    }
}

fun implicitCommandSchedulerSample() {
    val cmd = WaitCommand(5.seconds)

    // schedule implicitly links to DuckyScheduler's companion object
    cmd.schedule()
}

fun explicitCommandSchedulerSample(cs: CommandScheduler) {
    val cmd = WaitCommand(5.seconds)
    with(cs) {
        // schedule links to `cs` object
        cmd.schedule()
    }
}

fun triggerOnceOnSample(boolean1: Boolean, booleanSupplier: () -> Boolean) {
    // In ducklib triggers are just `() -> Boolean`-typed functions.
    ({ boolean1 }).onceOnTrue { println("hi") }
    ({ boolean1 }).onceOnTrue(WaitCommand(5.seconds))

    // meaning you can also do this
    booleanSupplier.onceOnTrue { println("hi") }
}

fun triggerWhileOnSample(boolean1: Boolean) {
    // In ducklib triggers are just `() -> Boolean`-typed functions.
    ({ boolean1 }).whileOnTrue({ println("rising edge") }) { println("falling edge") }
    ({ boolean1 }).whileOnTrue(WaitCommand(5.seconds))
}

fun triggerCombinationSample(
    boolean1: Boolean,
    boolean2: Boolean,
    booleanSupplier1: () -> Boolean,
    booleanSupplier2: () -> Boolean
) {
    ({ boolean1 && boolean2 }) // etc.
    // same as
    ({ boolean1 } and { boolean2 }) // etc.

    // similar but with functions
    (booleanSupplier1 and booleanSupplier2) // etc.
}

fun triggerInversionSample(boolean1: Boolean) {
    val boolean1trigger = ({ boolean1 })

    // this will activate on falling edge instead of rising now
    (!boolean1trigger).onceOnTrue { println("hi") }
}

fun commandGroupSample(command1: Command, command2: Command, command3: Command, command4: Command) {
    // A command group groups commands (shocker)

    // Parallel command groups run many commands all at the same time.
    // They can't share any requirements.
    (command1 with command2).schedule()

    // Sequential command groups run many commands in a sequence.
    // Unlike parallel command groups, these can share requirements.
    (command1 then command2).schedule()

    // Deadline command groups are like parallel command groups,
    // but they interrupt all commands when the deadline command finishes.
    (command1 deadlines command2).schedule()

    // Race command groups are like parallel command groups,
    // but they interrupt all commands as soon as one finishes.
    (command1 races command2).schedule()

    // With all command groups, use the `and` operator to add more commands.
    (command1 deadlines command2 and command3 and command4).schedule()
}

fun parallelCommandGroupSample(command1: Command, command2: Command, command3: Command) {
    // 3 ways of making commands run in parallel
    // 1: construct a ParallelCommandGroup (not recommended)
    ParallelCommandGroup(command1, command2).schedule()
    ParallelCommandGroup(command1, command2, command3).schedule()
    // 2: use the chaining `with` function
    command1.with(command2, command3).schedule()
    command1.with(command2).schedule()
    // 3: use the infix operator (most recommended):
    (command1 with command2).schedule()
    (command1 with command2 with command3).schedule()
    // equivalent to above, since `and` adds a command and in this context a second `with` would as well
    (command1 with command2 and command3).schedule()
    // NOTE: `with` is smart enough to construct a single ParallelCommandGroup and add the rest of the commands to it

    // even though it kind of looks like it'll be equivalent to
    ParallelCommandGroup(ParallelCommandGroup(command1, command2), command3)
    // it will actually end up being equivalent to 1
}

fun sequentialCommandGroupSample(command1: Command, command2: Command, command3: Command) {
    // 3 ways of making commands run in sequence
    // 1: construct a SequentialCommandGroup (not recommended)
    SequentialCommandGroup(command1, command2, command3).schedule()
    // 2: use the chaining `with` function
    command1.then(command2, command3).schedule()
    // 3: use the infix operator (most recommended):
    (command1 then command2).schedule()
    (command1 then command2 then command3).schedule()
    // equivalent to above, since `and` adds a command and in this context a second `then` would as well
    (command1 then command2 and command3).schedule()
    // NOTE: `then` is smart enough to construct a single SequentialCommandGroup and add the rest of the commands to it

    // even though it kind of looks like it'll be equivalent to
    SequentialCommandGroup(SequentialCommandGroup(command1, command2), command3)
    // it will actually end up being equivalent to 1
}

fun deadlineCommandGroupSample(command1: Command, command2: Command, command3: Command) {
    // 1: construct a DeadlineCommandGroup (ehh)
    DeadlineCommandGroup(command1, command2).schedule()
    DeadlineCommandGroup(command1, command2, command3).schedule()
    // 2: use the chaining `deadlineWith` function (most recommended)
    command1.deadlines(command2)
    command1.deadlines(command2, command3)
    // 3: use the infix operator
    (command1 deadlines command2)
    (command1 deadlines command2 and command3)
    // NOTE: `deadlines` is *not* chainable like `with` and `then`. Use `and` instead, as shown above.

    (command1 deadlines command2 deadlines command3)
    // equivalent to
    DeadlineCommandGroup(DeadlineCommandGroup(command1, command2), command3)
    // which is *technically* equivalent but not really.
}

fun raceCommandGroupSample(command1: Command, command2: Command, command3: Command) {
    // 1: construct a RaceCommandGroup (ehh)
    RaceCommandGroup(command1, command2).schedule()
    RaceCommandGroup(command1, command2, command3).schedule()
    // 2: use the chaining `deadlineWith` function (most recommended)
    command1.races(command2)
    command1.races(command2, command3)
    // 3: use the infix operator
    (command1 races command2)
    (command1 races command2 and command3)

    // vv NOT RECOMMENDED BUT TECHNICALLY WORKS vv
    (command1 races command2 races command3)
}

enum class State {
    STATE_1,
    STATE_2,
    STATE_3,
}

fun whenCommandSample() {
    // When this command gets initialized, it runs the selector lambda and chooses a command to run.
    WhenCommand { State.entries[Random.nextInt(0, 3)] }.configure {
        this[State.STATE_1] = { println("0") }.instant()
        this[State.STATE_2] = { println("1") }.instant()
        this[State.STATE_3] = { println("1") }.instant()
    }

    // alternatively
    WhenCommand(
        0 to { println("0") }.instant(),
        1 to { println("1") }.instant(),
        2 to { println("2") }.instant(),
    ) { Random.nextInt(0, 3) }
}