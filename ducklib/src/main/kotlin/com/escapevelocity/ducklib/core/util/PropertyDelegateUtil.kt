package com.escapevelocity.ducklib.core.util

import kotlin.reflect.KProperty

interface ValDelegate<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
}

interface VarDelegate<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
}