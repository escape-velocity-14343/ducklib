package com.escapevelocity.ducklib.ftc.extensions

import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.SerialNumber
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

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
    val initActions = ArrayList<() -> Unit>()
    var map: HardwareMap? = null
        private set
    val nonNullMap get() = map ?: error("Can't get hardware device before initialization")

    /**
     * Gets a device and casts it to type [T].
     *
     * This is possible without passing the class instance because of Kotlin's reified generics for inline functions.
     *
     * @param name The name of the device to get
     * @sample com.escapevelocity.ducklib.ftc.samples.hardwareMapSample
     * @see HardwareMap.get
     */
    inline operator fun <reified T : HardwareDevice> get(name: String) = nonNullMap.get(name) as? T

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
        nonNullMap.get(T::class.java, serialNumber)

    fun init(map: HardwareMap) {
        this.map = map;
        initActions.forEach { it() }
    }

    /**
     * Like [get], but it defers getting the hardware object until map initialization time.
     * Variables this applies to must be read-only.
     *
     * **NOTE**:
     * Since this is an inline function with reified type parameters, it may not behave as expected in all situations!
     * @sample com.escapevelocity.ducklib.ftc.samples.deferredHardwareMapSample
     */
    inline fun <reified T : HardwareDevice> deferred(
        name: String,
        crossinline config: T.() -> Unit = {},
    ): ReadOnlyProperty<Any?, T> {
        var value: T? = null
        val initAction = {
            value = (nonNullMap.get(name) ?: error("Couldn't find hardware device with name \"$name\"")) as? T
                ?: error("Hardware device with name \"$name\" is not of type ${T::class.simpleName}")
            value.config()
        }
        if (map == null) {
            initActions.add(initAction)
        } else {
            initAction()
        }
        return object : ReadOnlyProperty<Any?, T> {
            override fun getValue(thisRef: Any?, property: KProperty<*>) =
                value ?: error("Can't get hardware device before initialization")
        }
    }

    /**
     * Like [get], but it defers getting the hardware object until map initialization time.
     * Variables this applies to must be read-only.
     *
     * **NOTE**:
     * Since this is an inline function with reified type parameters, it may not behave as expected in all situations!
     * @sample com.escapevelocity.ducklib.ftc.samples.deferredHardwareMapSample
     */
    inline fun <reified T : HardwareDevice> deferred(
        serialNumber: SerialNumber,
        crossinline config: T.() -> Unit = {},
    ): ReadOnlyProperty<Any?, T> {
        var value: T? = null
        val initAction = {
            value = (nonNullMap.get(T::class.java, serialNumber)
                ?: error("Couldn't find hardware device with serial number \"$serialNumber\"")) as? T
                ?: error("Hardware device with serial number \"$serialNumber\" is not of type ${T::class.simpleName}")
            value.config()
        }
        if (map == null) {
            initActions.add(initAction)
        } else {
            initAction()
        }
        return object : ReadOnlyProperty<Any?, T> {
            override fun getValue(thisRef: Any?, property: KProperty<*>) =
                value ?: error("Can't get hardware device before initialization")
        }
    }

    inline fun <reified T> deferred(noinline supplier: () -> T): ReadOnlyProperty<Any?, T> {
        var value: T? = null
        val initAction = {
            value = supplier()
        }
        if (map == null) {
            initActions.add(initAction)
        } else {
            initAction()
        }
        return object : ReadOnlyProperty<Any?, T> {
            override fun getValue(thisRef: Any?, property: KProperty<*>) =
                value ?: error("Can't construct $supplier before initialization")
        }
    }
}
