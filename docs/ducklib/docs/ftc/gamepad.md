# Gamepad extensions

ducklib introduces some utility extension methods on `Gamepad`,
which together aim to create a more ergonomic interface for binding actions and commands to driver input.

Check out the [example TeleOp](/example_teleop.md) for some real-world situations.

## Button binding

You can bind an action to a button like this:

```kotlin
gamepad1[ButtonInput.TRIANGLE].onceOnTrue(cmd)
```

Now, if you've read the [triggers](/trigger/index.md) page,
you should know that triggers in ducklib are just `() -> Boolean`-typed functions.
This means that `#!kotlin gamepad1[ButtonInput.TRIANGLE]` is actually returning a lambda,
which means you can store it in a variable,
pass it around to other methods,
and do all the normal things you'd do to lambdas as this.
It also means you can get the current state of the input by calling it immediately:

```kotlin
val v = gamepad1[ButtonInput.TRIANGLE]()
```

although that's why `current` exists:

```kotlin
val v = gamepad1.current(ButtonInput.TRIANGLE)
```

so use that instead.

Most of the button names are pulled straight from the SDK,
although some are changed for clarity and convention.

## Analog and vector binding

Only getting button inputs returns a function type,
analog and vector gets return the actual value:

```kotlin
val driveCmd = LambdaCommand {
    addRequirements(drivetrainSubsystem)
    lmexecute = {
        drivetrainSubsystem.drive(
            driver[VectorInput.STICK_LEFT].flip(Axis.Y),
            driver[AnalogInput.STICK_X_LEFT].radians
        )
    }
    lmfinished = { false }
}
```

### Converting analog inputs into triggers

Sometimes you want to make a trigger that's based on a threshold,
say for when the gamepad trigger is depressed 50%.
You can wrap those in lambdas:

```kotlin
({ gamepad1[AnalogInput.STICK_Y_LEFT] > 0.5 }).onceOnTrue(cmd)
```

<details> <summary>Why the parenthesis?</summary>
They're to avoid parser ambiguities like this:

```kotlin
val idk = duck()

{ x }

// could be parsed as

/* statement  */ val idk = duck() { x }

// or

/* statement  */ val idk = duck()
/* expression */ { x }
```

since if a function has a function-typed parameter as the last parameter,
you can move the argument out of the parenthesis.

<p>

You can see that the latter is what we want,
but the compiler doesn't know that since it's trying to be as whitespace-agnostic as a Kotlin compiler can be.
</details>

Also, if you're okay with the default threshold of 0.5,
you can use the `bool` utility methods:

```kotlin
// triggers when value > 0.5
({ gamepad1[AnalogInput.STICK_Y_LEFT] }).bool().onceOnTrue(cmd)

// triggers when length > 0.5
({ gamepad1[VectorInput.STICK_LEFT] }).bool().onceOnTrue(cmd)
```
