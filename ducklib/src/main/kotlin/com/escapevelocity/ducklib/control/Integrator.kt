package com.escapevelocity.ducklib.control

/**
 * that moment when you need `∫f(t)dt`
 */
class Integrator : (Double) -> Double {
    private var lastNs = 0L
    var total: Double? = null
    override fun invoke(p1: Double): Double {
        val thisNs = System.nanoTime()
        val dt = (thisNs - lastNs) * 1e-9
        lastNs = thisNs

        if (total == null) {
            total = 0.0
            return total!!
        }

        // hey don't mutate total concurrently pls okay thx
        // (the reason for no smart cast :c)
        total = total!! + p1 * dt
        return total!!
    }

    fun reset() {
        total = null
    }
}