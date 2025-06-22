package com.escapevelocity.ducklib.ftc.drivers.sensorange

import com.escapevelocity.ducklib.core.geometry.Radians
import com.escapevelocity.ducklib.core.geometry.degrees
import com.escapevelocity.ducklib.core.geometry.times
import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.AnalogInputController
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.configuration.annotations.AnalogSensorType
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties

@AnalogSensorType
@DeviceProperties(
    name = "@string/configTypeSensOrangeAbsoluteEncoder",
    xmlTag = "SensOrangeAbsoluteEncoder",
    builtIn = false
)
class SensOrangeAbsoluteEncoder(controller: AnalogInputController, channel: Int) : AnalogInput(controller, channel) {
    var offset = Radians.Companion.ZERO
    var maxAngle = 360.degrees
    var maxReportedVoltage = 3.3
    var inverted = false

    val angle
        get() = (voltage * maxAngle / maxReportedVoltage).rotated(offset)

    override fun getManufacturer() = HardwareDevice.Manufacturer.Other
    override fun getDeviceName() = "sensOrange Absolute Encoder"
}
