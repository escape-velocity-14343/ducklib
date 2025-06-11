---
description: An overview of how hardware maps work in ducklib
---
# Hardware map extensions

## Key features

ducklib includes a hardware map extension class to make using the hardware map a little easier.
The main pain points that can be (and are) resolved with Kotlin are as follows

* Having to pass in a class instance, like `#!java hardwareMap.get(Servo::class, "servo")`
* No type inference
* Hardware access classes not being able to be final, since they get set in `initialize`

ducklib's `HardwareMapEx` class solves all of these problems.

## Usage

Create a variable named whatever you want at the OpMode-class-level:

```kotlin
class MyOpMode : LinearOpMode() {
    val map = HardwareMapEx()
    // etc.
}
```

<details>
    <summary>Why can't it be wrapped in the initializer?</summary>
        This is because it doesn't get fully set until the `OpMode` is initialized.
        Technically there are ways around that but it works fine without it,
        which you'll see in a bit.
</details>

Now, no `HardwareMap` instance has been wrapped yet,
and this is intentional.
If you're familiar to `lateinit var`s in Kotlin,
it's kind of similar to that.

To fully initialize the map, call `init`:

```kotlin
override fun runOpMode() {
    map.init(hardwareMap)
}
```

## Standard usage

Like the normal `HardwareMap`, `HardwareMapEx` supports get methods:

```kotlin
val servo: Servo = map.get<Servo>("servo")
```

However, note how you don't need to pass a `#!kotlin Servo::class` instance to `#!kotlin map.get`.
This is because that function uses a reified type parameter, significantly simplifying the code.
This also means you get some type inference:

```kotlin
val servo = map.get<Servo>("servo")
```

or

```kotlin
val servo: Servo = map["servo"]
```

but not

```kotlin
val servo = map.get["servo"]
```

since then no types are defined.
Personally, I prefer the second option since it more clearly communicates the type of the variable,
and it also allows you to use the more idiomatic subscripting operator.

## Deferred hardware references

Since `map` isn't initialized until `runOpMode`,
it makes it somewhat problematic to set references.
For example, in Java you'd have to do

```java
Servo servo;

@Override
void runOpMode() {
    servo = hardwareMap.get(Servo::class, "servo");
}
```

However, this moves initialization far from the ~~center of rotation~~ variable declaration,
which makes the code less readable.
That's why `HardwareMapEx` supports hardware-read delegated values:

```kotlin
val servo: Servo by map.deferred("servo")
```

The improvement here is that the types are closer together and only declared once,
and the hardware name of the servo is closer to the declaration of the variable.
This makes the code more readable overall,
since you have to hunt around for usages less.
But, you may ask, how does it know what the object is?

The Servo instance isn't actually configured until `map` is initialized with `init`!
When you call `map.init(hardwareMap)`,
it actually does two things

1. set the internal reference hardware map to the passed hardware map
2. **initialize all deferred properties**

Accessing a hardware map-delegated property before it's initialized *will* result in an exception!
Don't try to!

## Deferred initialization

> "But then",

you may ask,

> "how can you keep instancing of subsystems close to the declaration as well?
This:

```kotlin
val servo: Servo by map.deferred("servo")

val subsystem = ServoSubsystem(servo) // exception 🤾!
```

> won't work because it's accessing the hardware-referencing variable before it's declared."

ducklib has thought of that too.
Simply use the overload that accepts a `() -> T` instead of the normal hardware access overload like so:

```kotlin
val servo: Servo by map.deferred("servo")

val subsystem by map.deferred { ServoSubsystem(servo) }
```

Note how all the types are inferred,
keeping the code clean and concise.
