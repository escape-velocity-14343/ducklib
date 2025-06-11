---
description: An overview of how triggers work in ducklib
---

# Triggers

## Introduction

Unlike in FTCLib and NextFTC,
triggers in ducklib are any `#!kotlin () -> Boolean`-typed functions.
No need to wrap them in a class, just use them as-is.

## Binding actions and commands

Binding actions (`#!kotlin () -> Unit`-typed functions) to triggers involves using the [`TriggerScheduler`](/scheduler/index.md#triggerscheduler)'s various binding functions.
### `onceOnTrue`/`onceOnFalse`

These functions will run the action or schedule the command on the rising and falling edge of the supplier respectively.

### `whileOnTrue`

This function accepts an action to run on the rising edge and on the falling edge.
It's a convenience method for

```kotlin
trigger.onceOnTrue { /* do something */ }.onceOnFalse { /* do something else */ }
```

The command overload schedules the command on the rising edge and cancels it on the falling edge.
If you want it to do the opposite, invert the trigger:

```kotlin
(!trigger).whileOnTrue(cmd)
```

## Deferred variants

All the command-accepting `onceOn*` and `whileOnTrue` have deferred variants.
These delay construction of the command until trigger time.
This has some advantage over [`DeferredCommand`](/commands/builtin.md#deferredcommand).
It's a more complete solution that doesn't require copying the requirements exactly,
because the command construction happens *before* command initialization.
