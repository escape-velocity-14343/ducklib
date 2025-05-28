package com.escapevelocity.ducklib.core.test

import com.escapevelocity.ducklib.control.differentiate
import com.escapevelocity.ducklib.control.integrate
import com.escapevelocity.ducklib.control.pipe
import kotlin.test.Test

class ControlTest {
    val waitTime = 10L
    @Test
    fun testDifferentiator() {
        var a = 0.0
        val pipeline = { a } pipe differentiate()
        for (i in 1..10) {
            println(pipeline())
            a += waitTime / 1000.0
            Thread.sleep(waitTime)
        }
    }

    @Test
    fun testIntegrator() {
        var a = 1.0
        val pipeline = { a } pipe integrate()
        for (i in 1..100) {
            //a += 1000.0 / waitTime
            println(pipeline())
            Thread.sleep(waitTime)
        }
    }

    @Test
    fun testDifferentiatorIntegrator() {
        var a = 0.0
        val pipeline = { a } pipe integrate() pipe differentiate()
        for (i in 1..10) {
            a += 1000.0 / waitTime / 1000.0
            println(pipeline())
            Thread.sleep(waitTime)
        }
    }
}