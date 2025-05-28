package com.escapevelocity.ducklib.core.util

/**
 * Inverts a boolean supplier.
 */
operator fun <T : () -> Boolean> T.not() = { !this() }

/**
 * Composes two boolean suppliers with [kotlin.Boolean.and] logic.
 *
 * **NOTE**:
 * Both suppliers will be evaluated!
 * This *does not* short-circuit!
 */
infix fun <T : () -> Boolean> T.and(right: () -> Boolean) = { this() and right() }

/**
 * Composes two boolean suppliers with [kotlin.Boolean.or] logic.
 *
 * **NOTE**:
 * Both suppliers will be evaluated!
 * This *does not* short-circuit!
 */
infix fun <T : () -> Boolean> T.or(right: () -> Boolean) = { this() or right() }

/**
 * Composes two boolean suppliers with [kotlin.Boolean.xor] logic.
 */
infix fun <T : () -> Boolean> T.xor(right: () -> Boolean) = { this() xor right() }

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

fun <TIn, TMid, TOut> ((TIn) -> TMid).split(vararg pipelines: (TMid) -> TOut): Collection<(TIn) -> TOut> = pipelines.map { this pipe it }

