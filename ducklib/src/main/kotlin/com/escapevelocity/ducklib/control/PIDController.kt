package com.escapevelocity.ducklib.control

open class PIDController(val constants: PID, setpoint: () -> Double) : Controller<Double, Double, Double>(setpoint) {
    private var errorIntegral: Double = 0.0
    private var lastError: Double = 0.0

    override fun calculate(setpoint: Double, processVar: Double, delta: Double): Double {
        val error = setpoint - processVar
        val p = error
        errorIntegral += error * delta
        val i = errorIntegral
        val d = (lastError - error) / delta
        lastError = error
        return p * constants.kP + i * constants.kI + d * constants.kD
    }
}

open class PID(var kP: Double, var kI: Double, var kD: Double) {
    operator fun component1() = kP
    operator fun component2() = kI
    operator fun component3() = kD
}

class P(kP: Double) : PID(kP, 0.0, 0.0)