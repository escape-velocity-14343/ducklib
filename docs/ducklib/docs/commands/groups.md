# Command groups

## Introduction

Command groups are a special type of [composing command](/commands/builtin.md#compositions) that group together commands in useful ways.

## `SequentialCommandGroup`

The most common type of command group.
It runs a set of commands,
one after the other,
and finishes when the last one finishes.

For example,
a command that says "hi",
waits 5 seconds,
then says "bye":

```kotlin
val cmd = SequentialCommandGroup(
    { println("hi") }.instant(),
    WaitCommand(5.seconds),
    { println("bye") }.instant(),
)
```

You can also construct this with the `then` infix operator:

```kotlin
val cmd = { println("hi") }.instant() then WaitCommand(5.seconds) and { println("bye") }.instant()
```

## `ParallelCommandGroup`

The second most common type of command group.
It runs a set of commands all at the same time,
and finishes when they're all finished.

For example,
a command that says "hi" and "bye" at the same time:

```kotlin
val cmd = ParallelCommandGroup(
    { println("hi") }.instant(),
    { println("bye") }.instant(),
)
```

You can also construct this with the `with` infix operator:

```kotlin
val cmd = { println("hi") }.instant() with { println("bye") }.instant()
```

## `ParallelCommandGroup`-like groups

There are a bunch of groups that act similar to `ParallelCommandGroup` but have different end conditions.

### `RaceCommandGroup`

This group runs all the commands at the same time and ends when any one of them ends.

For example, a group that waits for 4 seconds:

```kotlin
val cmd = RaceCommandGroup(
    WaitCommand(4.seconds),
    WaitCommand(5.seconds),
)
```

You can also construct this with the `races` infix operator:

```kotlin
val cmd = WaitCommand(4.seconds) races WaitCommand(5.seconds) and WaitCommand(6.seconds)
```

### `DeadlineCommandGroup`

This group ends when the "deadline" command finishes.

For example, a group that runs for exactly 5 seconds all the while printing "duck":

```kotlin
val cmd = DeadlineCommandGroup(
    WaitCommand(5.seconds),
    { println("duck") }.loop()
)
```

A group that runs for 5 seconds but only prints "duck" onceL

```kotlin
val cmd = DeadlineCommandGroup(
    WaitCommand(5.seconds),
    { println("duck") }.instant()
)
```

You can also construct this with the `deadlines` operator.
Note that unlike the `then`, `with`, `parallel`, and `races` operators,
you *have* to use `and` to chain multiple commands in the group.

```kotlin
val cmd = WaitCommand(5.seconds) deadlines { println("duck") }.loop()
```

Also, `DeadlineCommandGroup`s may seem very similar to `RaceCommandGroup` but they are not the same.
`RaceCommandGroup` ends when *any* of the commands end,
but `DeadlineCommandGroup` only ends when the *deadline* command ends.

## Infix operators

Command groups also have some utility infix operators that make your command groups read more like English.
They are as follows:

* `then`: SequentialCommandGroup
* `with`: ParallelCommandGroup
* `races`: RaceCommandGroup
* `deadlines`: DeadlineCommandGroup
* `and`: Adds the command on the right to the command group on the left

Like all Kotlin infix operators,
they all have the same precedence and are left-associative.
This means that

```kotlin
cmd1 then cmd2 with cmd3 and cmd4 deadlines cmd5
```

is parsed as

```kotlin
(((cmd1 then cmd2) with cmd3) and cmd4) deadlines cmd5
```

and no other way.

`then`, `with`, and `races` will automatically check if the command on the left is a group of the right type,
and if it is, it adds the command on the right instead of creating nested groups.
However, this doesn't work with DeadlineCommandGroup because the command on the left is always the deadline command,
so you have to use `and` with that one.

In my opinion,
`and` makes the code easier to read,
so I suggest using it.
For example, this:

```kotlin
cmd1 with cmd2 and cmd3 and cmd3
```

is more readable than

```kotlin
cmd1 with cmd2 with cmd3 with cmd4
```
