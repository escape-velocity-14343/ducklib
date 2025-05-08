package com.escapevelocity.ducklib.ftc.extensions

import com.escapevelocity.ducklib.core.geometry.Vector2
import com.escapevelocity.ducklib.core.geometry.inches
import com.qualcomm.robotcore.hardware.Gamepad

/**
 * Get a button input supplier.
 *
 * **NOTE**:
 * Returns a supplier, not a value!
 * If you want a value, use [current]
 * @param input The [ButtonInput]'s state to produce
 * @sample com.escapevelocity.ducklib.ftc.samples.gamepadSample
 */
operator fun Gamepad.get(input: ButtonInput) = { current(input) }

/**
 * Gets the current value of an [AnalogInput]
 *
 * @param input The [AnalogInput]'s state to get
 * @sample com.escapevelocity.ducklib.ftc.samples.gamepadSample
 */
operator fun Gamepad.get(input: AnalogInput) = current(input)

/**
 * Gets the current value of an [VectorInput]
 *
 * @param input The [VectorInput]'s state to get
 * @sample com.escapevelocity.ducklib.ftc.samples.gamepadSample
 */
operator fun Gamepad.get(input: VectorInput) = current(input)

fun Gamepad.current(input: ButtonInput) = when (input) {
    ButtonInput.DPAD_UP -> dpad_up
    ButtonInput.DPAD_DOWN -> dpad_down
    ButtonInput.DPAD_LEFT -> dpad_left
    ButtonInput.DPAD_RIGHT -> dpad_right
    ButtonInput.A -> a
    ButtonInput.B -> b
    ButtonInput.X -> x
    ButtonInput.Y -> y
    ButtonInput.CROSS -> cross
    ButtonInput.CIRCLE -> circle
    ButtonInput.SQUARE -> square
    ButtonInput.TRIANGLE -> triangle
    ButtonInput.BUMPER_LEFT -> left_bumper
    ButtonInput.BUMPER_RIGHT -> right_bumper
    ButtonInput.TOUCHPAD_PRESS -> touchpad
    ButtonInput.TOUCHPAD_TOUCH -> touchpad_finger_1
    ButtonInput.TOUCHPAD_TOUCH_FINGER_2 -> touchpad_finger_2
    ButtonInput.PLAYSTATION_BUTTON -> ps
    ButtonInput.GUIDE -> guide
    ButtonInput.SHARE -> share
    ButtonInput.OPTIONS -> options
    ButtonInput.STICK_BUTTON_LEFT -> left_stick_button
    ButtonInput.STICK_BUTTON_RIGHT -> right_stick_button
}

fun Gamepad.current(input: AnalogInput) = when (input) {
    AnalogInput.TRIGGER_LEFT -> (left_trigger.toDouble())
    AnalogInput.TRIGGER_RIGHT -> (right_trigger.toDouble())
    AnalogInput.STICK_X_LEFT -> (left_stick_x.toDouble())
    AnalogInput.STICK_Y_LEFT -> (left_stick_y.toDouble())
    AnalogInput.STICK_X_RIGHT -> (right_stick_x.toDouble())
    AnalogInput.STICK_Y_RIGHT -> (right_stick_y.toDouble())
    AnalogInput.TOUCHPAD_X -> (touchpad_finger_1_x.toDouble())
    AnalogInput.TOUCHPAD_Y -> (touchpad_finger_1_y.toDouble())
    AnalogInput.TOUCHPAD_X_FINGER_2 -> (touchpad_finger_2_x.toDouble())
    AnalogInput.TOUCHPAD_Y_FINGER_2 -> (touchpad_finger_2_y.toDouble())
}

fun Gamepad.current(input: VectorInput) = when (input) {
    VectorInput.STICK_LEFT -> Vector2(
        left_stick_x.toDouble().inches,
        left_stick_y.toDouble().inches
    )

    VectorInput.STICK_RIGHT -> Vector2(
        right_stick_x.toDouble().inches,
        right_stick_y.toDouble().inches
    )

    VectorInput.TOUCHPAD -> Vector2(
        touchpad_finger_1_x.toDouble().inches,
        touchpad_finger_1_y.toDouble().inches
    )

    VectorInput.TOUCHPAD_FINGER_2 -> Vector2(
        touchpad_finger_2_x.toDouble().inches,
        touchpad_finger_2_y.toDouble().inches
    )
}

fun (() -> Double).boolean(predicate: (Double) -> Boolean = { it > 0.5 }) = { predicate(this()) }
fun (() -> Vector2).boolean(predicate: (Vector2) -> Boolean = { it.lengthSquared > 0.5 * 0.5 }) = { predicate(this()) }

enum class ButtonInput {
    DPAD_UP, DPAD_DOWN, DPAD_LEFT, DPAD_RIGHT,

    A, B, X, Y,

    CROSS, CIRCLE, SQUARE, TRIANGLE,

    BUMPER_LEFT, BUMPER_RIGHT,

    TOUCHPAD_PRESS, TOUCHPAD_TOUCH, TOUCHPAD_TOUCH_FINGER_2,

    /**
     * "PS4 Support - PS Button" whatever this means
     */
    PLAYSTATION_BUTTON,

    /**
     * "button guide - often the large button in the middle of the controller. The OS may capture this button before
     * it is sent to the app; in which case you'll never receive it" so maybe don't use this one?
     */
    GUIDE,

    SHARE, OPTIONS,

    STICK_BUTTON_LEFT, STICK_BUTTON_RIGHT,
}

enum class AnalogInput {
    TRIGGER_LEFT, TRIGGER_RIGHT,

    STICK_X_LEFT, STICK_Y_LEFT,

    STICK_X_RIGHT, STICK_Y_RIGHT,

    TOUCHPAD_X, TOUCHPAD_Y, TOUCHPAD_X_FINGER_2, TOUCHPAD_Y_FINGER_2,
}

enum class VectorInput {
    STICK_LEFT, STICK_RIGHT,
    TOUCHPAD, TOUCHPAD_FINGER_2,
}