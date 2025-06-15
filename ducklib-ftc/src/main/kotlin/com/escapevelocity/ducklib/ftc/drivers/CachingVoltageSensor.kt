package com.escapevelocity.ducklib.ftc.drivers

import com.escapevelocity.ducklib.ftc.drivers.CachingVoltageSensor.nominalVoltage
import com.escapevelocity.ducklib.ftc.drivers.CachingVoltageSensor.voltage
import com.escapevelocity.ducklib.ftc.extensions.HardwareMapEx
import com.qualcomm.robotcore.hardware.VoltageSensor
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object CachingVoltageSensor {
    private lateinit var sensor: VoltageSensor
    private var lastReadTime = 0L

    /**
     * This is the voltage we want the robot to always try to operate at.
     * */
    var nominalVoltage: Double = 12.5
    private var lastVoltage = nominalVoltage

    /**
     * How long a reading can stay alive.
     * After this duration elapses,
     * the cache is invalidated and a new value must be read.
     */
    var cacheInvalidateTime: Duration = 0.5.seconds

    val voltage
        get(): Double {
            lastVoltage =
                if (System.nanoTime() - lastReadTime > cacheInvalidateTime.inWholeNanoseconds) sensor.voltage else lastVoltage
            return lastVoltage
        }

    fun initialize(map: HardwareMapEx) {
        sensor = map.map?.getAll(VoltageSensor::class.java)?.first() ?: error("no voltage sensor found???")
    }
}

fun normalize(power: Double) = power * voltage / voltage
