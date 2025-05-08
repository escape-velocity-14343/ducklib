package com.escapevelocity.ducklib.ftc.samples

import com.escapevelocity.ducklib.core.command.commands.WaitCommand
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler.Companion.onceOnTrue
import com.escapevelocity.ducklib.core.command.subsystem.Subsystem
import com.escapevelocity.ducklib.ftc.extensions.ButtonInput
import com.escapevelocity.ducklib.ftc.extensions.HardwareMapEx
import com.escapevelocity.ducklib.ftc.extensions.get
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import kotlin.time.Duration.Companion.seconds

fun gamepadSample(gamepad: Gamepad, slideSubsystem: SlideSubsystem) {
    gamepad[ButtonInput.STICK_BUTTON_LEFT].onceOnTrue { println("hi") }
    gamepad[ButtonInput.STICK_BUTTON_LEFT].onceOnTrue(WaitCommand(5.seconds))
}

fun triggerSample(slideSubsystem: SlideSubsystem) {
    slideSubsystem.retracted.onceOnTrue { println("slides are retracted") }
    // or
    ({ slideSubsystem.retractedVal }).onceOnTrue { println("slides are retracted") }
}

class SlideSubsystem : Subsystem() {
    val retracted
        get() = { extendedInches < 5.0 }

    val retractedVal
        get() = extendedInches < 5.0

    var extendedInches = 0.0

    override fun periodic() {
        extendedInches = 0.0
    }
}

fun deferredHardwareMapSample(hardwareMap: HardwareMap) {
    val map = HardwareMapEx()
    val motor: DcMotor by map.deferred("hi")

    // call in the initialization stage of opmode, all deferred references are now initialized and shouldn't be null
    map.init(hardwareMap)
    motor.power = 0.5
}

fun hardwareMapSample(hardwareMap: HardwareMap) {
    // construct an extended hardware map
    val map = HardwareMapEx()
    map.init(hardwareMap)

    val motor: DcMotor = map["hi"]!!
}