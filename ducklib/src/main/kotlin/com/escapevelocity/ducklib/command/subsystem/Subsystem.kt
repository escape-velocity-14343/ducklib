package com.escapevelocity.ducklib.command.subsystem

abstract class Subsystem {
    open val name = javaClass.simpleName
    abstract fun periodic()
}
