---
description: An overview of how commands work in ducklib
---

# Commands

## Overview

A command represents an action.
For example, an action that waits for 2 seconds:

```kotlin
val cmd = WaitCommand(2.0.seconds)
```

A command that runs a specified action:

```kotlin
val cmd = InstantCommand {  println("ducky!") }
```
More info on [built-in commands](builtin.md)

A user-made command that drives the robot around:

```kotlin
val cmd = DriveCommand(mecanum, Vector2(3.inches, 5.inches))
```
More info on [custom commands](custom.md)

A command that runs all of those commands in sequence

```kotlin
val cmd = SequentialCommandGroup(
    WaitCommand(2.0.seconds),
    InstantCommand {  println("ducky!") },
    DriveCommand(mecanum, Vector2(3.inches, 5.inches)),
)
```
More info on [command groups](groups.md)

Commands are extremely flexible because each one represents a single unit of work,
and they can be composed in groups to make entire autonomi.

Commands are run by the [command scheduler](/scheduler/index.md).
Commands can also optionally have a set of [requirements and a priority](/scheduler/conflicts.md).

## Lifetime functions

Every command overrides a set of *lifetime functions,* which (in calling order) are:

### `initialize`

This gets called as soon as the command gets actually scheduled,
so if the scheduling gets deferred
(e.g. by attempting to schedule it while the scheduler is processing commands)
it'll wait until the deferred calls get run.
It gets run *every* time the command is initialized, not just the first time.

### `execute`

This gets called every tick.
Put recurring actions in here,
such as PID loops.

### `finished`

This gets checked after executing, and if it's `#!kotlin true`,
the command finishes and stops getting run and is descheduled.
This is not the deepest thing in the world.

### `suspend` and `resume`

These get called when the command scheduler suspends a command and resumes it, respectively.
Suspension can happen for a variety of reasons,
which are documented [here](/scheduler/conflicts.md).

### `end`

This gets called when the command ends (😱).
If `#!kotlin canceled` is `#!kotlin true`,
that means another command interrupted this one.

### Conflict resolution stuff

For more info about the conflict resolution system, read [conflicts](/scheduler/conflicts.md).

## Configuration

For one-off configurations, there exists

```kotlin
fun <T : Command> T.setPriority(priority: Priority): T
fun <T : Command> T.setOnHigherConflict(onHigherConflict: OnHigherConflict): T
fun <T : Command> T.setOnEqualConflict(onEqualConflict: OnEqualConflict): T
```

which you can call like

```kotlin
val cmd = WaitCommand(3.seconds).setPriority(5.priority)
```

Note the usage of generics for this;
those ensure that the "in" type always matches the "out" type,
if you imagine it as sort of a pipeline.
This means that if you do the above, the result will have the correct type
(`WaitCommand`, in this case)
instead of the more generic `Command`.

There's also

```kotlin
inline fun <T : Command> T.configure(configuration: T.() -> Unit): T
```

which uses the idiomatic [builder syntax](https://kotlinlang.org/docs/type-safe-builders.html) to easily configure a command's properties.
Anything you put inside the function will have an implicit `this` of the command you're calling it on,
which makes it easy to set multiple properties in the same block without having to chain excessively:

```kotlin
val cmd = WaitCommand(4.seconds).configure {
    priority = 5.priority
    onHigherConflict = OnHigherConflict.CANCEL
    name = "My wait command :)"
}
```

Compare that to the equivalent code using the "standard" inline configuration API:

```kotlin
val cmd = WaitCommand(4.seconds)
    .setPriority(5.priority)
    .setOnHigherConflict(OnHigherConflict.CANCEL)
    .setName("My wait command :)")
```
🤢

*it's honestly not that bad, but it's more well suited to setting single properties, not 3 all at once.
Plus, you can define your own methods with receivers to set up standard configurations and pass them to `configure` as a reference,
which you can't do with the other one.*
