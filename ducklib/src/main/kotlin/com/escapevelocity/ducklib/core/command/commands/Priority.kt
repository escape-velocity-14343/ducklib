package com.escapevelocity.ducklib.core.command.commands

/**
 * Represents the priority of a command.
 *
 * Commands with higher priority will be scheduled over commands with lower priority, according to
 * [Command.onHigherConflict] and [Command.onEqualConflict]
 *
 * A higher integer value corresponds to a higher priority.
 *
 * @property priority The integer representation of the priority level.
 */
@JvmInline
value class Priority(val priority: Int) : Comparable<Priority> {
    override fun compareTo(other: Priority) = priority.compareTo(other.priority)

    companion object {
        val LOWEST = Int.MIN_VALUE.priority
    }
}

val Int.priority
    get() = Priority(this)