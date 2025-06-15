package com.escapevelocity.ducklib.ftc.drivers

import android.util.Log
import com.escapevelocity.ducklib.core.geometry.*
import com.qualcomm.hardware.lynx.LynxI2cDeviceSynch
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.I2cAddr
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice
import com.qualcomm.robotcore.hardware.I2cDeviceSynchSimple
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties
import com.qualcomm.robotcore.hardware.configuration.annotations.I2cDeviceType
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.TypeConversion
import java.nio.ByteBuffer
import java.nio.ByteOrder

@I2cDeviceType
@DeviceProperties(
    name = "goBILDA® Pinpoint Odometry Computer",
    xmlTag = "goBILDAPinpoint",
    description = "goBILDA® Pinpoint Odometry Computer (IMU Sensor Fusion for 2 Wheel Odometry)"
)
class GoBildaPinpoint(
    deviceClient: I2cDeviceSynchSimple,
    deviceClientIsOwned: Boolean,
    private val speed: LynxI2cDeviceSynch.BusSpeed = LynxI2cDeviceSynch.BusSpeed.STANDARD_100K,
) : I2cDeviceSynchDevice<I2cDeviceSynchSimple>(deviceClient, deviceClientIsOwned) {
    private var internalDeviceStatus = 0

    /**
     * Checks the Odometry Computer's most recent loop time.
     *
     * If values less than 500, or more than 1100 are commonly seen here, there may be something
     * wrong with your device. Please reach out to tech@gobilda.com
     *
     * @return loop time in microseconds (1/1,000,000 seconds)
     */
    var loopTime: Int = 0
        private set

    /**
     * @return the raw value of the X (forward) encoder in ticks
     */
    var encoderX: Int = 0
        private set

    /**
     * @return the raw value of the Y (strafe) encoder in ticks
     */
    var encoderY: Int = 0
        private set
    private var xPosition = 0f
    private var yPosition = 0f
    private var hOrientation = 0f
    private var xVelocity = 0f
    private var yVelocity = 0f
    private var hVelocity = 0f// estimated from 2 poses internal to driver but it works better

    //return new Pose2D(DistanceUnit.MM, xVelocity, yVelocity, AngleUnit.RADIANS, hVelocity);

    private val loop = ElapsedTime()

    init {
        deviceClient.i2cAddress = I2cAddr.create7bit(DEFAULT_ADDRESS.toInt())
        registerArmingStateCallback(false)
    }

    override fun getManufacturer() = HardwareDevice.Manufacturer.Other

    @Synchronized
    override fun doInitialize(): Boolean {
        ((deviceClient) as LynxI2cDeviceSynch).setBusSpeed(speed)
        return true
    }

    override fun getDeviceName(): String {
        return "goBILDA® Pinpoint Odometry Computer"
    }

    /**
     * Register map of the i2c device
     */
    private enum class Register(val bVal: Int) {
        DEVICE_ID(1),
        DEVICE_VERSION(2),
        DEVICE_STATUS(3),
        DEVICE_CONTROL(4),
        LOOP_TIME(5),
        X_ENCODER_VALUE(6),
        Y_ENCODER_VALUE(7),
        X_POSITION(8),
        Y_POSITION(9),
        H_ORIENTATION(10),
        X_VELOCITY(11),
        Y_VELOCITY(12),
        H_VELOCITY(13),
        MM_PER_TICK(14),
        X_POD_OFFSET(15),
        Y_POD_OFFSET(16),
        YAW_SCALAR(17),
        BULK_READ(18)
    }

    // Device Status enum that captures the current fault condition of the device
    enum class DeviceStatus(val status: Int) {
        NOT_READY(0),
        READY(1),
        CALIBRATING(1 shl 1),
        FAULT_X_POD_NOT_DETECTED(1 shl 2),
        FAULT_Y_POD_NOT_DETECTED(1 shl 3),
        FAULT_NO_PODS_DETECTED(
            1 shl 2 or (1 shl 3)
        ),
        FAULT_IMU_RUNAWAY(1 shl 4)
    }

    // enum that captures the direction the encoders are set to
    enum class EncoderDirection {
        FORWARD,
        REVERSED
    }

    // enum that captures the kind of goBILDA odometry pods, if goBILDA pods are used
    enum class GoBildaOdometryPods {
        GOBILDA_SWINGARM_POD,
        GOBILDA_4_BAR_POD
    }

    // enum that captures a limited scope of read data. More options may be added in future update
    enum class ReadData {
        ONLY_UPDATE_HEADING,
    }

    /**
     * Writes an int to the i2c device
     *
     * @param reg the register to write the int to
     * @param i the integer to write to the register
     */
    private fun writeInt(reg: Register, i: Int) {
        deviceClient.write(reg.bVal, TypeConversion.intToByteArray(i, ByteOrder.LITTLE_ENDIAN))
    }

    /**
     * Reads an int from a register of the i2c device
     *
     * @param reg the register to read from
     * @return returns an int that contains the value stored in the read register
     */
    private fun readInt(reg: Register): Int {
        return TypeConversion.byteArrayToInt(deviceClient.read(reg.bVal, 4), ByteOrder.LITTLE_ENDIAN)
    }

    /**
     * Converts a byte array to a float value
     *
     * @param byteArray byte array to transform
     * @param byteOrder order of byte array to convert
     * @return the float value stored by the byte array
     */
    private fun byteArrayToFloat(byteArray: ByteArray, byteOrder: ByteOrder): Float {
        return ByteBuffer.wrap(byteArray).order(byteOrder).getFloat()
    }

    /**
     * Reads a float from a register
     *
     * @param reg the register to read
     * @return the float value stored in that register
     */
    private fun readFloat(reg: Register): Float {
        return byteArrayToFloat(deviceClient.read(reg.bVal, 4), ByteOrder.LITTLE_ENDIAN)
    }

    /**
     * Converts a float to a byte array
     *
     * @param value the float array to convert
     * @return the byte array converted from the float
     */
    private fun floatToByteArray(value: Float, byteOrder: ByteOrder): ByteArray {
        return ByteBuffer.allocate(4).order(byteOrder).putFloat(value).array()
    }

    /**
     * Writes a byte array to a register on the i2c device
     *
     * @param reg the register to write to
     * @param bytes the byte array to write
     */
    private fun writeByteArray(reg: Register, bytes: ByteArray) {
        deviceClient.write(reg.bVal, bytes)
    }

    /**
     * Writes a float to a register on the i2c device
     *
     * @param reg the register to write to
     * @param f the float to write
     */
    private fun writeFloat(reg: Register, f: Float) {
        val bytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(f).array()
        deviceClient.write(reg.bVal, bytes)
    }

    /**
     * Looks up the DeviceStatus enum corresponding with an int value
     *
     * @param s int to lookup
     * @return the Odometry Computer state
     */
    private fun lookupStatus(s: Int): DeviceStatus {
        if ((s and DeviceStatus.CALIBRATING.status) != 0) {
            return DeviceStatus.CALIBRATING
        }
        val xPodDetected = (s and DeviceStatus.FAULT_X_POD_NOT_DETECTED.status) == 0
        val yPodDetected = (s and DeviceStatus.FAULT_Y_POD_NOT_DETECTED.status) == 0

        if (!xPodDetected && !yPodDetected) {
            return DeviceStatus.FAULT_NO_PODS_DETECTED
        }
        if (!xPodDetected) {
            return DeviceStatus.FAULT_X_POD_NOT_DETECTED
        }
        if (!yPodDetected) {
            return DeviceStatus.FAULT_Y_POD_NOT_DETECTED
        }
        if ((s and DeviceStatus.FAULT_IMU_RUNAWAY.status) != 0) {
            return DeviceStatus.FAULT_IMU_RUNAWAY
        }
        if ((s and DeviceStatus.READY.status) != 0) {
            return DeviceStatus.READY
        } else {
            return DeviceStatus.NOT_READY
        }
    }

    /**
     * Call this once per loop to read new data from the Odometry Computer. Data will only update
     * once this is called.
     */
    fun update() {
        try {
            val bArr = deviceClient.read(Register.BULK_READ.bVal, 40)
            internalDeviceStatus = TypeConversion.byteArrayToInt(bArr.copyOfRange(0, 4), ByteOrder.LITTLE_ENDIAN)
            loopTime = TypeConversion.byteArrayToInt(bArr.copyOfRange(4, 8), ByteOrder.LITTLE_ENDIAN)
            this.encoderX = TypeConversion.byteArrayToInt(bArr.copyOfRange(8, 12), ByteOrder.LITTLE_ENDIAN)
            this.encoderY = TypeConversion.byteArrayToInt(bArr.copyOfRange(12, 16), ByteOrder.LITTLE_ENDIAN)
            xPosition = byteArrayToFloat(bArr.copyOfRange(16, 20), ByteOrder.LITTLE_ENDIAN)
            yPosition = byteArrayToFloat(bArr.copyOfRange(20, 24), ByteOrder.LITTLE_ENDIAN)
            hOrientation = byteArrayToFloat(bArr.copyOfRange(24, 28), ByteOrder.LITTLE_ENDIAN)
            xVelocity = byteArrayToFloat(bArr.copyOfRange(28, 32), ByteOrder.LITTLE_ENDIAN)
            yVelocity = byteArrayToFloat(bArr.copyOfRange(32, 36), ByteOrder.LITTLE_ENDIAN)
            hVelocity = byteArrayToFloat(bArr.copyOfRange(36, 40), ByteOrder.LITTLE_ENDIAN)
        } catch (e: Exception) {
            // fix for I2C_STUCK_IN_STOP error - not part of default driver!
            Log.w("Pinpoint", "Pinpoint read failed (NACK). Using stale data.")
        }
    }

    /**
     * Call this once per loop to read new data from the Odometry Computer. This is an override of
     * the update() function which allows a narrower range of data to be read from the device for
     * faster read times. Currently ONLY_UPDATE_HEADING is supported.
     *
     * @param data GoBildaPinpoint.readData.ONLY_UPDATE_HEADING
     */
    fun update(data: ReadData) {
        if (data == ReadData.ONLY_UPDATE_HEADING) {
            hOrientation = byteArrayToFloat(
                deviceClient.read(Register.H_ORIENTATION.bVal, 4), ByteOrder.LITTLE_ENDIAN
            )
        }
    }

    /**
     * Recalibrates the Odometry Computer's internal IMU.
     *
     * **Robot MUST be stationary**
     *
     * The device takes a large number of samples, and uses those as the gyroscope zero-offset. This
     * takes approximately 0.25 seconds.
     */
    fun recalibrateIMU() {
        writeInt(Register.DEVICE_CONTROL, 1 shl 0)
    }

    /**
     * Resets the current position to 0,0,0 and recalibrates the Odometry Computer's internal IMU.
     *
     * **Robot MUST be stationary**
     *
     * The device takes a large number of samples, and uses those as the gyroscope zero-offset. This
     * takes approximately 0.25 seconds.
     */
    fun resetPosAndIMU() {
        writeInt(Register.DEVICE_CONTROL, 1 shl 1)
    }

    /**
     * Can reverse the direction of each encoder.
     *
     * @param xEncoder The direction for the X encoder. X (forward) pod should increase when the robot is moving
     * forward
     * @param yEncoder The direction for the Y encoder.Y (strafe) pod should increase when the robot is moving
     * left
     */
    fun setEncoderDirections(xEncoder: EncoderDirection, yEncoder: EncoderDirection) {
        writeInt(
            Register.DEVICE_CONTROL, 1 shl when (xEncoder) {
                EncoderDirection.FORWARD -> 5
                EncoderDirection.REVERSED -> 4
            }
        )

        writeInt(
            Register.DEVICE_CONTROL, 1 shl when (yEncoder) {
                EncoderDirection.FORWARD -> 3
                EncoderDirection.REVERSED -> 2
            }
        )
    }

    /**
     * If you're using goBILDA odometry pods, the ticks-per-mm values (see [setEncoderResolution]) are stored here for easy
     * access.
     */
    fun setEncoderResolution(pods: GoBildaOdometryPods) = setEncoderResolution(
        when (pods) {
            GoBildaOdometryPods.GOBILDA_SWINGARM_POD -> GOBILDA_SWINGARM_POD
            GoBildaOdometryPods.GOBILDA_4_BAR_POD -> GOBILDA_4_BAR_POD
        }
    )

    /**
     * Sets the encoder resolution in ticks per mm of the odometry pods.
     *
     * You can find this number by dividing the counts-per-revolution of your encoder by the
     * circumference of the wheel.
     *
     * @param ticksPerMm should be somewhere between 10 ticks/mm and 100 ticks/mm. A goBILDA
     * Swingarm pod is ~13.26291192 ticks/mm
     */
    fun setEncoderResolution(ticksPerMm: Float) {
        writeByteArray(
            Register.MM_PER_TICK, (floatToByteArray(ticksPerMm, ByteOrder.LITTLE_ENDIAN))
        )
    }

    /**
     * Checks the deviceID of the Odometry Computer. Should return 1.
     *
     * Returns 1 if the device is functional.
     */
    val deviceID: Int
        get() = readInt(Register.DEVICE_ID)

    /**
     * The firmware version of the Odometry Computer
     */
    val deviceVersion: Int
        get() = readInt(Register.DEVICE_VERSION)

    var yawScalar: Float
        set(value) = writeByteArray(
            Register.YAW_SCALAR, floatToByteArray(value, ByteOrder.LITTLE_ENDIAN)
        )
        get() = readFloat(Register.YAW_SCALAR)

    /**
     * Device Status stores any faults the Odometry Computer may be experiencing. These faults
     * include:
     *
     * @return one of the following states:
     * * [DeviceStatus.NOT_READY] - The device is currently powering up. And has not initialized yet. RED LED
     * * [DeviceStatus.READY] - The device is currently functioning as normal. GREEN LED
     * * [DeviceStatus.CALIBRATING] - The device is currently recalibrating the gyro. RED LED
     * * [DeviceStatus.FAULT_NO_PODS_DETECTED] - the device does not detect any pods plugged in. PURPLE LED
     * * [DeviceStatus.FAULT_X_POD_NOT_DETECTED] - The device does not detect an X pod plugged in. BLUE LED
     * * [DeviceStatus.FAULT_Y_POD_NOT_DETECTED] - The device does not detect a Y pod plugged in. ORANGE LED
     */
    val deviceStatus
        get() = lookupStatus(internalDeviceStatus)

    /**
     * Checks the Odometry Computer's most recent loop frequency.
     *
     * If values less than 900 or more than 2000 are commonly seen here, there may be something
     * wrong with your device. Please reach out to [tech@gobilda.com](mailto:tech@gobilda.com)
     */
    val frequency: Double
        get() = when (loopTime) {
            0 -> 0.0
            else -> 1_000_000.0 / loopTime
        }

    /**
     * The estimated X (forward) and Y (strafe) position of the robot
     */
    val pos
        get() = Vector2(xPosition.mm, yPosition.mm)

    /**
     * The estimated H (heading) position of the robot in Radians
     */
    val heading
        get() = hOrientation.radians

    /**
     * The estimated velocity of the robot in mm/sec
     */
    val vel
        get() = Vector2(xVelocity.mm, yVelocity.mm)

    /**
     * The estimated H (heading) velocity of the robot in radians/sec
     */
    val headingVelocity
        get() = hVelocity.radians

    /**
     * The user-set offset for the X (forward) pod
     *
     * **NOTE**: This uses its own I2C read, avoid calling this every loop.
     */
    var xOffset
        /**
         * Tuning this value should be unnecessary.
         * The goBILDA Odometry Computer has a per-device tuned yaw offset already applied when you
         * receive it.
         *
         * This is a scalar that is applied to the gyro's yaw value. Increasing it will mean it will
         * report more than one degree for every degree the sensor fusion algorithm measures.
         *
         * You can tune this variable by rotating the robot a large amount (10 full turns is a good
         * starting place) and comparing the amount that the robot rotated to the amount measured.
         * Rotating the robot exactly 10 times should measure 3600°. If it measures more or less, divide
         * moved amount by the measured amount and apply that value to the Yaw Offset.
         *
         * If you find that to get an accurate heading number you need to apply a scalar of more than
         * 1.05, or less than 0.95, your device may be bad. Please reach out to tech@gobilda.com
         *
         * @param value A scalar for the robot's heading.
         */
        set(value) = writeFloat(Register.X_POD_OFFSET, value.inches.toFloat())
        get() = readFloat(Register.X_POD_OFFSET).inches

    /**
     * The user-set offset for the Y (strafe) pod
     *
     * **NOTE**: This uses its own I2C read, avoid calling this every loop.
     */
    var yOffset
        set(value) = writeFloat(Register.Y_POD_OFFSET, value.inches.toFloat())
        get() = readFloat(Register.Y_POD_OFFSET).inches

    /**
     * A Pose2D containing the estimated position of the robot
     */
    var pose
        set(value) {
            writeByteArray(
                Register.X_POSITION, floatToByteArray(value.x.mm.toFloat(), ByteOrder.LITTLE_ENDIAN)
            )
            writeByteArray(
                Register.Y_POSITION, floatToByteArray(value.y.mm.toFloat(), ByteOrder.LITTLE_ENDIAN)
            )
            writeByteArray(
                Register.H_ORIENTATION, floatToByteArray(
                    value.heading.radians.toFloat(), ByteOrder.LITTLE_ENDIAN
                )
            )
        }
        get() = Pose2(pos, heading)

    companion object {
        private const val GOBILDA_SWINGARM_POD = 13.26291192f // ticks-per-mm for the goBILDA Swingarm Pod
        private const val GOBILDA_4_BAR_POD = 19.89436789f // ticks-per-mm for the goBILDA 4-Bar Pod

        // i2c address of the device
        const val DEFAULT_ADDRESS: Byte = 0x31
    }
}
