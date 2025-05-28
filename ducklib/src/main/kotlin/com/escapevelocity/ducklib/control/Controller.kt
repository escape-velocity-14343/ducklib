package com.escapevelocity.ducklib.control

abstract class Controller<TProcessVar, TSetpoint, TOut>(private val setpoint: () -> TSetpoint) : (TProcessVar) -> TOut {
    private var lastNs = 0L
    override fun invoke(p1: TProcessVar): TOut {
        val thisNs = System.nanoTime()
        val dt = (thisNs - lastNs) * 1e-9
        lastNs = thisNs
        return calculate(setpoint(), p1, dt)
    }

    abstract fun calculate(setpoint: TSetpoint, processVar: TProcessVar, delta: Double): TOut
}