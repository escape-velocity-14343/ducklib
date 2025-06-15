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
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.Servo

private class ExampleOpMode : LinearOpMode() {
    // **NOTE**: No HardwareMap actually exists, so this is sort of like an "empty wrapper"
    val map = HardwareMapEx()

    // defer construction of the Servo object until the HardwareMapEx is initialized
    val servo: Servo by map.deferred("motor1")

    // defer construction of DrivetrainSubsystem object until the HardwareMapEx is initialized
    val drivetrainSubsystem by map.deferred { DrivetrainSubsystem(map) }

    override fun runOpMode() {
        // initializing the HardwareMapEx also initializes all deferred fields like `servo`
        map.init(hardwareMap)

        // alias gamepad1 to 'driver' to make things easier to understand
        val driver = gamepad1 as Gamepad

        // ButtonInputs return suppliers which can be used with onceOnTrue and onceOnFalse directly
        driver[ButtonInput.A]
            .onceOnTrue({ servo.position = 0.5 }.instant(servo))
            .onceOnFalse({ servo.position = 0.0 }.instant(servo))

        // use a lambda command here
        // so we can capture the driver pad directly without having to pass in a DoubleSupplier
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

        waitForStart()

        while (!isStopRequested) {
            DuckyScheduler.run()
            telemetry.addLine(DuckyScheduler.toString())
            telemetry.update()
        }

        DuckyScheduler.reset()
    }
}

private class DrivetrainSubsystem(map: HardwareMapEx) : Subsystem() {
    val flMotor: DcMotor by map.deferred("flMotor")
    val frMotor: DcMotor by map.deferred("frMotor")
    val blMotor: DcMotor by map.deferred("blMotor")
    val brMotor: DcMotor by map.deferred("brMotor")

    fun drive(power: Pose2) {
        val (x, y, h) = power.xyh
        flMotor.power = x.v - y.v - h.radians
        frMotor.power = x.v + y.v + h.radians
        blMotor.power = x.v + y.v - h.radians
        brMotor.power = x.v - y.v + h.radians
    }

    fun drive(translationPower: Vector2, headingPower: Radians) =
        drive(Pose2(translationPower, headingPower))
}