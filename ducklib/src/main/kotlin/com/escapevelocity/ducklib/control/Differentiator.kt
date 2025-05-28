package com.escapevelocity.ducklib.control

/**
 * that moment when you need `d/dx f(x)`
 */
class Differentiator : (Double) -> Double {
    private var lastNs = 0L
    private var last: Double? = null
    override fun invoke(p1: Double): Double {
        val thisNs = System.nanoTime()
        val dt = (thisNs - lastNs) * 1e-9
        lastNs = thisNs
        if (last == null) {
            last = p1
        }
        val ddt = (p1 - last!!) / dt
        last = p1
        return ddt
    }
}