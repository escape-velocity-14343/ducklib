package com.escapevelocity.ducklib.core.util

/**
 * This is like [ClosedRange] but it lets you set the start and end which the other one doesn't let you do.
 *
 * For some reason.
 */
class ClosedRangeT<T : Comparable<T>>(override val start: T, override val endInclusive: T) : ClosedRange<T>

/**
 * This is like [ClosedRange] but it lets you set the start and end which the other one doesn't let you do.
 *
 * For some reason.
 */
class OpenRangeT<T : Comparable<T>>(override val start: T, override val endExclusive: T) : OpenEndRange<T>