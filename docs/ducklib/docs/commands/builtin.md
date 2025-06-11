---
description: An overview of the built-in commands that ship with ducklib, with usage examples
---
# Built-in commands

ducklib comes with a number of commands built in as basic utilities.

## Atomic commands

### `InstantCommand`

It runs a lambda once in [`execute`](/commands/index.md#execute) and then stops.

Example usage:

```kotlin
val cmd = InstantCommand { println("duck") }
```

Alternatively, with the decorator:

```kotlin
val cmd = { println("duck") }.instant()
```

You can also give both of these a set of requirements:

```kotlin
val cmd = InstantCommand(requirement) { println("duck") }
```

```kotlin
val cmd = { println("duck") }.instant(requirement)
```

### `LoopCommand`

*Not* to be confused with [`RepeatCommand`](#repeatcommand),
it calls the given lambda repeatedly forever.
Think if it like a non-instant `InstantCommand`.

Example usage:

```kotlin
val cmd = LoopCommand { println("duck") }
```

Alternatively, with the decorator:

```kotlin
val cmd = { println("duck") }.loop()
```

You can also give both of these a set of requirements:

```kotlin
val cmd = LoopCommand(requirement) { println("duck") }
```

```kotlin
val cmd = { println("duck") }.loop(requirement)
```

If you want to repeat a lambda finitely, compose an `InstantCommand` inside a `RepeatCommand` instead.

### `NoOpCommand`

it does nothing, like me.

### `WaitCommand`

It waits for a given number of seconds,
optionally accounting for suspension time as well
(e.g. if it has a 2 second timer,
has been running for 1 second,
gets suspended for 5 seconds,
then resumes,
it'll continue waiting for 1 second)

### `WaitUntilCommand`

It waits until a condition is true.

### `LambdaCommand`

A command that delegates the functions to lambda-typed properties.

It's useful for creating inline one-off commands that don't really get used anywhere else.

For example, a driving command that captures a subsystem reference and gamepad in the lambda:

```kotlin
LambdaCommand {
    execute = {
        // driver gamepad references don't need suppliers since it's wrapped in a lambda
        drivetrainSubsystem.drive(
            driver[VectorInput.STICK_LEFT].flip(Axis.Y),
            driver[AnalogInput.STICK_X_LEFT].radians
        )
    }
    finished = { false }
    config = {
        // add the requirements of the drivetrain subsystem
        // so that other commands that share that will suspend this command
        addRequirements(drivetrainSubsystem)
    }
}.schedule()
```

Note the `config = {`.
This is needed because,
for improved naming,
when you call the `LambdaCommand(configuration)` constructor,
the configuration lambda's receiver object is actually of the type `LambdaCommandBuilder` which aliases some of the properties of `LambdaCommand`.
This is because the function-typed variables are prefixed with `lm-` to avoid name overlaps in `LambdaCommand`.
However, this means that you can't configure the `Command`-specific properties like requirements and priority in the lambda.
To fix this, simply put those inside of `config`,
which will get called with the receiver of the constructed `LambdaCommand`:

```kotlin
LambdaCommand {
    // LambdaCommandBuilder - aliased names
    finished = { true }
    config = {
        // LambdaCommand - non-aliased names.
        // Note that this runs *after* the rest of the configuration is copied over.
        priority = 5.priority
        addRequirements(ss1)
    }
}
```

You can also capture state inside of the configuration lambda,
removing the need for a `StatefulLambda` [like Mercurial has](https://docs.dairy.foundation/Mercurial/commands/lambda):

```kotlin
LambdaCommand {
    var i = 0
    initialize = { i = 0 }
    execute = { i += 1 }
    finished = { i >= 5 }
}
```

## Compositions
Composition commands *compose* a set of commands,
inheriting most of their functionality while overriding specific actions.

### `DeferredCommand`
`DeferredCommand` defers command construction (through `commandSupplier`) until [initialization](/commands/index.md#initialize) time.
It's useful when you're making a command that has dynamically changing values,
such as a path generator.

Example usage:
```kotlin
val cmd = DeferredCommand { WaitCommand(getSomeDynamicWaitTime()) }
```
In this example,
construction of the `WaitCommand` is deferred until the command is initialized,
so even though `WaitCommand` doesn't accept a time supplier,
the amount of time it waits can change.
Note how this is fundamentally different from
```kotlin
val cmd = WaitCommand(getSomeDynamicWaitTime())
```
since that will compute the wait time once and use that every time it's scheduled.

### `IfCommand`, `IfElseCommand`, and `WhenCommand`
`IfCommand` runs a command if,
at [initialization](/commands/index.md#initialize) time,
the provided supplier returns `true`.

Example usage:
```kotlin
val cmd = IfCommand({ slidesRetracted }, extend())
```
In this example,
if `slidesRetracted` is `true`,
if the command is run something will extend,
otherwise nothing will happen.

`IfElseCommand` runs a command if,
at [initialization](/commands/index.md#initialize) time,
the provided supplier returns `true`,
otherwise it runs the other command.

Example usage:
```kotlin
val cmd = IfCommand({ slidesRetracted }, extend(), retract())
```
In this example,
if `slidesRetracted` is `true`,
if the command is run something will extend,
otherwise something will retract.

`WhenCommand` selects a command to run based on the provided supplier.

Example usage:
```kotlin
val cmd = WhenCommand { state }.configure {
    this[State.READY] = outtake()
    this[State.INTAKE] = retract() then outtake()
    this[State.HANG] = retract() then outtake()
    default = retract()
}
```
*For more information on the `configure` method, see [configure](/commands/index.md#configuration)*

It also has an alternative syntax:

```kotlin
val cmd = WhenCommand(
    State.READY to outtake(),
    State.INTAKE to retract() then outtake(),
    State.HANG to retract() then outtake(),
    default = retract()
) { state }
```

### `TimeoutCommand`

`TimeoutCommand` adds a timeout to the command.

Example usage

```kotlin
val cmd = TimeoutCommand(WaitCommand(5.seconds), 3.seconds)
```

Alternatively, use the decorator:

```kotlin
val cmd = WaitCommand(5.seconds).withTimeout(3.seconds)
```

### `RepeatCommand`

`RepeatCommand` runs a command repeatedly,
either for some finite number of repeats,
or forever.

Example usage:

```kotlin
// repeat 3 times
val cmd = RepeatCommand(WaitCommand(3.seconds), 3)
```

```kotlin
// repeat infinite times
val cmd = RepeatCommand(WaitCommand(3.seconds))
```

Alternatively, use the decorator:

```kotlin
// repeat 3 times
val cmd = WaitCommand(3.seconds).repeat(3)
```

```kotlin
// repeat infinite times
val cmd = WaitCommand(3.seconds).forever
```

This is especially useful for `InstantCommand`s:

```kotlin
val cmd = { println("duck!") }.instant().forever
```

### Groups
See [command groups](groups.md)
