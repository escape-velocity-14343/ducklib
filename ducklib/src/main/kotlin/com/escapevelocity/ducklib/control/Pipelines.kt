package com.escapevelocity.ducklib.control

import kotlin.math.max
import kotlin.math.min

fun differentiate(): (Double) -> Double {
    var lastNs = 0L
    var last: Double? = null
    return {
        val thisNs = System.nanoTime()
        val dt = (thisNs - lastNs) * 1e-9
        lastNs = thisNs
        if (last == null) {
            last = it
        }
        val ddt = (it - last!!) / dt
        last = it
        ddt
    }
}

fun integrate(): (Double) -> Double {
    var lastNs = 0L
    var total: Double? = null
    return {
        val thisNs = System.nanoTime()
        val dt = (thisNs - lastNs) * 1e-9
        lastNs = thisNs

        if (total == null) {
            total = 0.0
            total!!
        }

        // hey don't mutate total concurrently pls okay thx
        // (the reason for no smart cast :c)
        total = total!! + it * dt
        total
    }
}

fun derivativeLimiter(limit: Double): (Double) -> Double {
    var lastNs = 0L
    var last: Double? = null
    return { velocity ->
        val thisNs = System.nanoTime()
        val dt = (thisNs - lastNs) * 1e-9
        lastNs = thisNs
        if (last == null) {
            last = velocity
        }
        val x = min(max(velocity, last!! - limit * dt), last!! + limit * dt)
        last = x
        x
    }
}

fun <T : Comparable<T>> limiter(minValue: T? = null, maxValue: T? = null) = { it: T ->
    when {
        minValue != null && it < minValue -> minValue
        maxValue != null && it > maxValue -> maxValue
        else -> it
    }
}

/**
 * "Pipes" the result of one function into the other,
 * like a UNIX pipe.
 * It's equivalent to `f(g(x))`, or if you like math,
 * `(f∘g)(x)`.
 * It can be used to build functional pipelines with controllers and other operators.
 */
infix fun <TIn, TMid, TOut> ((TIn) -> TMid).pipe(x: (TMid) -> TOut): (TIn) -> TOut = { x(this(it)) }

/**
 * "Pipes" the result of one function into the other,
 * like a UNIX pipe.
 * It's equivalent to `f(g(x))`, or if you like math,
 * `(f∘g)(x)`.
 * It can be used to build functional pipelines with controllers and other operators.
 */
@JvmName("pipeNoInput")
infix fun <TMid, TOut> (() -> TMid).pipe(x: (TMid) -> TOut): () -> TOut = { x(this()) }

/**
 * "Pipes" the result of one function into the other,
 * like a UNIX pipe.
 * It's equivalent to `f(g(x))`, or if you like math,
 * `(f∘g)(x)`.
 * It can be used to build functional pipelines with controllers and other operators.
 */
@JvmName("pipeTerminal")
infix fun <TIn, TMid> ((TIn) -> TMid).pipe(x: (TMid) -> Unit): (TIn) -> Unit = { x(this(it)) }

fun <TIn, TMid, TOut> ((TIn) -> TMid).split(vararg pipelines: (TMid) -> TOut): Collection<(TIn) -> TOut> =
    pipelines.map { this pipe it }

infix fun <TIn, TOut> Collection<(TIn) -> TOut>.combine(combiner: (accumulator: TOut, element: TOut) -> TOut): (TIn) -> TOut =
    { input -> map { it(input) }.reduce(combiner) }
infix fun <TIn, TOut> Collection<(TIn) -> TOut>.combineIndexed(combiner: (index: Int, accumulator: TOut, element: TOut) -> TOut): (TIn) -> TOut =
    { input -> map { it(input) }.reduceIndexed(combiner) }

fun Collection<(Double) -> Double>.add() = combine { accumulator, element -> accumulator + element }

fun Collection<(Double) -> Double>.add(vararg weights: Double): (Double) -> Double {
    require(weights.size == size) { "Weights collection must be the same size as the controller collection" }
    return combineIndexed { index, accumulator, element -> accumulator + element * weights[index] }
}

fun Collection<(Double) -> Double>.mul() = combine { accumulator, element -> accumulator * element }
