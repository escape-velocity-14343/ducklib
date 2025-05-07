package com.escapevelocity.ducklib.core.command.subsystem

/**
 * The base Subsystem.
 *
 * Subsystems registered with the [com.escapevelocity.ducklib.core.command.scheduler.CommandScheduler] have their
 * [periodic] called every [com.escapevelocity.ducklib.core.command.scheduler.CommandScheduler.run] tick
 */
abstract class Subsystem {
    open val name: String = javaClass.simpleName
    open fun periodic() {}
}
