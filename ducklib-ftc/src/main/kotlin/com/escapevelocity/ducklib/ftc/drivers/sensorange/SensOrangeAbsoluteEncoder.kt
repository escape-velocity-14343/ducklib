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
    var offset = Radians.ZERO
    var maxAngle = 360.degrees
    var maxReportedVoltage = 3.3
    var inverted = false

    private var lastAngle = Radians.ZERO

    val angle
        get() = (voltage * maxAngle / maxReportedVoltage).rotated(offset).let(if (inverted) { it -> -it } else { it -> it })
    var totalAngle = Radians.ZERO
        private set
        get() {
            if (field == Radians.ZERO) {
                field = angle
                lastAngle = angle
            }
            field += angle.angleTo(lastAngle)
            lastAngle = angle
            return field
        }

    override fun getManufacturer() = HardwareDevice.Manufacturer.Other
    override fun getDeviceName() = "sensOrange Absolute Encoder"
}
