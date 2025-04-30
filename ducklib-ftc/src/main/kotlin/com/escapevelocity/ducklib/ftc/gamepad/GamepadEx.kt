package com.escapevelocity.ducklib.ftc.gamepad

import com.escapevelocity.ducklib.core.geometry.Vector2
import com.qualcomm.robotcore.hardware.Gamepad

class GamepadEx(val gamepad: Gamepad) {
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
        STICK_LEFT, STICK_RIGHT, TOUCHPAD, TOUCHPAD_FINGER_2,
    }

    fun button(button: ButtonInput) = { buttonVal(button) }

    fun buttonVal(button: ButtonInput) = when (button) {
        ButtonInput.DPAD_UP -> gamepad.dpad_up
        ButtonInput.DPAD_DOWN -> gamepad.dpad_down
        ButtonInput.DPAD_LEFT -> gamepad.dpad_left
        ButtonInput.DPAD_RIGHT -> gamepad.dpad_right
        ButtonInput.A -> gamepad.a
        ButtonInput.B -> gamepad.b
        ButtonInput.X -> gamepad.x
        ButtonInput.Y -> gamepad.y
        ButtonInput.CROSS -> gamepad.cross
        ButtonInput.CIRCLE -> gamepad.circle
        ButtonInput.SQUARE -> gamepad.square
        ButtonInput.TRIANGLE -> gamepad.triangle
        ButtonInput.BUMPER_LEFT -> gamepad.left_bumper
        ButtonInput.BUMPER_RIGHT -> gamepad.right_bumper
        ButtonInput.TOUCHPAD_PRESS -> gamepad.touchpad
        ButtonInput.TOUCHPAD_TOUCH -> gamepad.touchpad_finger_1
        ButtonInput.TOUCHPAD_TOUCH_FINGER_2 -> gamepad.touchpad_finger_2
        ButtonInput.PLAYSTATION_BUTTON -> gamepad.ps
        ButtonInput.GUIDE -> gamepad.guide
        ButtonInput.SHARE -> gamepad.share
        ButtonInput.OPTIONS -> gamepad.options
        ButtonInput.STICK_BUTTON_LEFT -> gamepad.left_stick_button
        ButtonInput.STICK_BUTTON_RIGHT -> gamepad.right_stick_button
    }

    fun analog(input: AnalogInput) = { analogVal(input) }

    fun analogVal(input: AnalogInput) = when (input) {
        AnalogInput.TRIGGER_LEFT -> (gamepad.left_trigger.toDouble())
        AnalogInput.TRIGGER_RIGHT -> (gamepad.right_trigger.toDouble())
        AnalogInput.STICK_X_LEFT -> (gamepad.left_stick_x.toDouble())
        AnalogInput.STICK_Y_LEFT -> (gamepad.left_stick_y.toDouble())
        AnalogInput.STICK_X_RIGHT -> (gamepad.right_stick_x.toDouble())
        AnalogInput.STICK_Y_RIGHT -> (gamepad.right_stick_y.toDouble())
        AnalogInput.TOUCHPAD_X -> (gamepad.touchpad_finger_1_x.toDouble())
        AnalogInput.TOUCHPAD_Y -> (gamepad.touchpad_finger_1_y.toDouble())
        AnalogInput.TOUCHPAD_X_FINGER_2 -> (gamepad.touchpad_finger_2_x.toDouble())
        AnalogInput.TOUCHPAD_Y_FINGER_2 -> (gamepad.touchpad_finger_2_y.toDouble())
    }

    fun vector(input: VectorInput) = { vectorVal(input) }

    fun vectorVal(input: VectorInput) = when (input) {
        VectorInput.STICK_LEFT -> Vector2(gamepad.left_stick_x.toDouble(), gamepad.left_stick_y.toDouble())

        VectorInput.STICK_RIGHT -> Vector2(gamepad.right_stick_x.toDouble(), gamepad.right_stick_y.toDouble())

        VectorInput.TOUCHPAD -> Vector2(gamepad.touchpad_finger_1_x.toDouble(), gamepad.touchpad_finger_1_y.toDouble())

        VectorInput.TOUCHPAD_FINGER_2 -> Vector2(
            gamepad.touchpad_finger_2_x.toDouble(),
            gamepad.touchpad_finger_2_y.toDouble()
        )
    }
}

fun (() -> Double).boolean(predicate: (Double) -> Boolean = { it > 0.5 }) = { predicate(this()) }
fun (() -> Vector2).boolean(predicate: (Vector2) -> Boolean = { it.lengthSquared > 0.5 * 0.5 }) = { predicate(this()) }