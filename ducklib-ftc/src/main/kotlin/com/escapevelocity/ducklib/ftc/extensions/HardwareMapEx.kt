package com.escapevelocity.ducklib.ftc.extensions

import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.SerialNumber
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.cast

/**
 * A wrapper class for [HardwareMap].
 *
 * It supports reified type parameters so you don't need to pass Java classes everywhere with [get] and late-initialized
 * property delegates through [deferred].
 *
 * @sample com.escapevelocity.ducklib.ftc.samples.hardwareMapSample
 * @sample com.escapevelocity.ducklib.ftc.samples.deferredHardwareMapSample
 */
class HardwareMapEx() {
    private val properties = ArrayList<HardwareMapProperty<*>>()
    var map: HardwareMap? = null

    /**
     * Gets a device and casts it to type [T].
     *
     * This is possible without passing the class instance because of Kotlin's reified generics for inline functions.
     *
     * @param name The name of the device to get
     * @sample com.escapevelocity.ducklib.ftc.samples.hardwareMapSample
     * @see HardwareMap.get
     */
    inline operator fun <reified T : HardwareDevice> get(name: String) = map?.get(name) as? T

    /**
     * Gets a device and casts it to type [T].
     *
     * This is possible without passing the class instance because of Kotlin's reified generics for inline functions.
     *
     * @param serialNumber The name of the device to get
     * @sample com.escapevelocity.ducklib.ftc.samples.hardwareMapSample
     * @see HardwareMap.get
     */
    inline operator fun <reified T : HardwareDevice> get(serialNumber: SerialNumber) =
        map?.get(T::class.java, serialNumber)

    fun init(map: HardwareMap) {
        this.map = map;
        for (property in properties) property.init()
    }

    /**
     * Don't construct this yourself, use [com.escapevelocity.ducklib.ftc.extensions.HardwareMapEx.deferred]
     *
     * Apparently this has to be public because of inline functions or something, but I didn't want to make it public
     * @param T The type of the hardware device (e.g. [com.qualcomm.robotcore.hardware.DcMotorEx])
     */
    class HardwareMapProperty<T : HardwareDevice>(val clazz: KClass<T>, val name: Any, val map: HardwareMapEx) {
        init {
            map.properties += this;
        }

        var device: T? = null
        operator fun getValue(thisRef: Any?, property: KProperty<*>): T = clazz.cast(device!!)
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {}
        fun init() {
            device = device ?: when (name) {
                is String -> clazz.cast(map[name])
                is SerialNumber -> map.map?.get(clazz.java, name)
                else -> throw IllegalArgumentException("Only String and SerialNumber are allowed")
            }
        }

        companion object {
            inline fun <reified T : HardwareDevice> create(name: Any, map: HardwareMapEx) =
                HardwareMapProperty(T::class, name, map)
        }
    }

    /**
     * Constructs a [HardwareMapProperty] variable delegate from the given [T] and [name]
     *
     * **NOTE**:
     * Since this is an inline function with reified type parameters, it may not behave as expected in all situations!
     * @sample com.escapevelocity.ducklib.ftc.samples.deferredHardwareMapSample
     */
    inline fun <reified T : HardwareDevice> deferred(name: String) =
        HardwareMapProperty.create<T>(name, this)

    /**
     * Constructs a [HardwareMapProperty] variable delegate from the given [T] and [serialNumber]
     *
     * **NOTE**:
     * Since this is an inline function with reified type parameters, it may not behave as expected in all situations!
     * @sample com.escapevelocity.ducklib.ftc.samples.deferredHardwareMapSample
     */
    inline fun <reified T : HardwareDevice> deferred(serialNumber: SerialNumber) =
        HardwareMapProperty.create<T>(serialNumber, this)
}