package com.escapevelocity.ducklib.core.command.subsystem

abstract class Subsystem {
    open val name: String = javaClass.simpleName
    open fun periodic() {}
}
