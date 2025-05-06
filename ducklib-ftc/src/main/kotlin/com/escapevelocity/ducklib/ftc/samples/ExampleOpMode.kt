package com.escapevelocity.ducklib.ftc.samples

import com.escapevelocity.ducklib.core.command.commands.LambdaCommand
import com.escapevelocity.ducklib.core.command.commands.instant
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler.Companion.onceOnFalse
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler.Companion.onceOnTrue
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler.Companion.schedule
import com.escapevelocity.ducklib.core.command.subsystem.Subsystem
import com.escapevelocity.ducklib.core.geometry.*
import com.escapevelocity.ducklib.ftc.extensions.*
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo

class ExampleOpMode : OpMode() {
    val map = HardwareMapEx()
    val driver by lazy { gamepad1.ex }
    val operator by lazy { gamepad2.ex }

    val servo: Servo by map.deferred("motor1")

    val drivetrainSubsystem by lazy { DrivetrainSubsystem(map) }

    override fun init() {
        map.init(hardwareMap)

        driver[ButtonInput.A]
            .onceOnTrue({ servo.position = 0.5 }.instant(servo))
            .onceOnFalse({ servo.position = 0.0 }.instant(servo))

        drivetrainSubsystem.driveCommand(
            { driver[VectorInput.STICK_LEFT].flip(Axis.Y) },
            { driver[AnalogInput.STICK_X_LEFT].radians }
        ).schedule()
    }

    override fun loop() {
        DuckyScheduler.run()
    }
}

class DrivetrainSubsystem(map: HardwareMapEx) : Subsystem() {
    val flMotor: DcMotor by map.deferred("flMotor")
    val frMotor: DcMotor by map.deferred("frMotor")
    val blMotor: DcMotor by map.deferred("blMotor")
    val brMotor: DcMotor by map.deferred("brMotor")

    fun drive(power: Pose2) {
        val (x, y, h) = power.xyh
        flMotor.power = x.v - y.v - h.v
        frMotor.power = x.v + y.v + h.v
        blMotor.power = x.v + y.v - h.v
        brMotor.power = x.v - y.v + h.v
    }

    fun drive(translationPower: Vector2, headingPower: Radians) = drive(Pose2(translationPower, headingPower))

    fun driveCommand(xyPower: () -> Vector2, headingPower: () -> Radians) = LambdaCommand(this) {
        lmexecute = {
            drive(xyPower(), headingPower())
        }
        lmfinished = { false }
    }
}