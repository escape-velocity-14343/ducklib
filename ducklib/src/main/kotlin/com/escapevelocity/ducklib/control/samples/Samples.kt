package com.escapevelocity.ducklib.control.samples

import com.escapevelocity.ducklib.control.P
import com.escapevelocity.ducklib.control.PIDController
import com.escapevelocity.ducklib.core.command.commands.composition.forever
import com.escapevelocity.ducklib.core.command.commands.instant
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler.Companion.schedule
import com.escapevelocity.ducklib.core.util.pipe

private fun pipelineSample() {
    // create a PIDController pipeline directly
    val pid = { motorPos } pipe PIDController(P(0.5)) { setpoint } pipe { motorPower = it }

    // create a slightly different PIDController pipeline
    val pid2 = PIDController(P(0.5)) { setpoint } pipe { motorPower = it }
    // now you can "tick" these by calling them

    pid()
    pid2(motorPos)

    // or make them run forever as a command
    pid.instant().forever.schedule()
}

private val motorPos: Double = 0.0
private var motorPower: Double = 0.0
private var setpoint: Double = 0.0