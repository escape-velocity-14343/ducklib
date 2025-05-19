# Schedulers

There are two types of schedulers in ducklib,
`TriggerScheduler`s and `CommandScheduler`s.

## `TriggerScheduler`

The `TriggerScheduler` handles running actions on a [trigger](/triggers/index.md)'s rising edge and falling edge.
This can be used to bind actions to gamepad inputs,
state changes,
and similar things.

For example, using the gamepad extension API:
```kotlin
driver[ButtonInput.A]
    .onceOnTrue({ servo.position = 0.5 }.instant(servo))
    .onceOnFalse({ servo.position = 0.0 }.instant(servo))
```

Here, a command is created inline and bound to a trigger.

## `CommandScheduler`

The `CommandScheduler` handles running [commands](/commands/index.md),
and gracefully handling [requirement conflicts](/scheduler/conflicts.md) as they arise.

## `DuckyScheduler`

`DuckyScheduler` is the default implementation of both of these in ducklib.
It fully implements the priority and requirement systems in ducklib.

You can use the companion instance:

```kotlin
// the rest of the imports are omitted
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler.Companion.onceOnFalse
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler.Companion.onceOnTrue
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler.Companion.schedule

class MyOpMode : OpMode {
    fun initialize() {
        val cmd = NoOpCommand()
        // uses the DuckyScheduler companion instance
        cmd.schedule()
    }
}
```

or make your own:

```kotlin
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler.Companion.onceOnFalse
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler.Companion.onceOnTrue
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler.Companion.schedule

class MyOpMode : OpMode {
    val cs = DuckyScheduler()

    fun initialize() {
        val cmd = NoOpCommand()
        // uses the custom instance
        with (cs) {
            cmd.schedule()
        }
    }
}
```

This is commonly done for thread-safety,
since `DuckyScheduler` isn't thread-safe by itself.
