package com.escapevelocity.ducklib.command.subsystem

abstract class Subsystem {
    open val name: String = javaClass.simpleName
    open fun periodic() {}
}
