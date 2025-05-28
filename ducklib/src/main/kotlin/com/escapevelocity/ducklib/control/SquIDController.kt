package com.escapevelocity.ducklib.control

import com.escapevelocity.ducklib.core.math.signedPow

class SquIDController(constants: PID, setpoint: () -> Double) : PIDController(constants, setpoint) {
    override fun calculate(setpoint: Double, processVar: Double, delta: Double): Double {
        return super.calculate(setpoint, processVar, delta).signedPow(1.0 / 2.0)
    }
}