package com.escapevelocity.ducklib.core.test

import com.escapevelocity.ducklib.control.Differentiator
import com.escapevelocity.ducklib.control.Integrator
import com.escapevelocity.ducklib.core.util.pipe
import kotlin.test.Test

class ControlTest {
    val waitTime = 10L
    @Test
    fun testDifferentiator() {
        var a = 0.0
        val pipeline = { a } pipe Differentiator()
        for (i in 1..10) {
            println(pipeline())
            a += waitTime / 1000.0
            Thread.sleep(waitTime)
        }
    }

    @Test
    fun testIntegrator() {
        var a = 1.0
        val pipeline = { a } pipe Integrator()
        for (i in 1..100) {
            //a += 1000.0 / waitTime
            println(pipeline())
            Thread.sleep(waitTime)
        }
    }

    @Test
    fun testDifferentiatorIntegrator() {
        var a = 0.0
        val pipeline = { a } pipe Integrator() pipe Differentiator()
        for (i in 1..10) {
            a += 1000.0 / waitTime / 1000.0
            println(pipeline())
            Thread.sleep(waitTime)
        }
    }
}