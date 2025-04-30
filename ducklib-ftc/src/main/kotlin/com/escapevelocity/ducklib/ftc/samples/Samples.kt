package com.escapevelocity.ducklib.ftc.samples

import com.escapevelocity.ducklib.core.command.commands.WaitCommand
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler.Companion.onceOnTrue
import com.escapevelocity.ducklib.core.command.subsystem.Subsystem
import com.escapevelocity.ducklib.ftc.gamepad.GamepadEx

fun gamepadSample(gp: GamepadEx, slideSubsystem: SlideSubsystem) {
    gp.button(GamepadEx.ButtonInput.STICK_BUTTON_LEFT).onceOnTrue {
        println("hi")
    }

    gp.button(GamepadEx.ButtonInput.STICK_BUTTON_LEFT).onceOnTrue(WaitCommand(5.0))
    ({ slideSubsystem.retracted }).onceOnTrue { println("slides are retracted") }
    // new Trigger(() -> slideSubsystem.retracted).whileActive(InstantCommand(() -> System.out.println("slides are retracted")))
}

class SlideSubsystem : Subsystem() {
    //val retracted
    //    get() = { extendedInches < 5.0 }

    val retracted
        get() = extendedInches < 5.0

    var extendedInches = 0.0

    override fun periodic() {
        extendedInches = 0.0
    }
}