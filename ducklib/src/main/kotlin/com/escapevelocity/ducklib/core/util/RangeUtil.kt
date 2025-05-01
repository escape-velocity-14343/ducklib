package com.escapevelocity.ducklib.core.util

class ClosedRangeT<T : Comparable<T>>(override val start: T, override val endInclusive: T) : ClosedRange<T>
class OpenRangeT<T : Comparable<T>>(override val start: T, override val endExclusive: T) : OpenEndRange<T>