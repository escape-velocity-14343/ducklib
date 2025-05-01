package com.escapevelocity.ducklib.core.samples

import com.escapevelocity.ducklib.core.command.commands.*
import com.escapevelocity.ducklib.core.command.scheduler.CommandScheduler
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler.Companion.onceOnTrue
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler.Companion.schedule
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler.Companion.whileOnTrue
import com.escapevelocity.ducklib.core.util.and
import com.escapevelocity.ducklib.core.util.not

fun inlineCommandConfigurationSample() {
    val cmd = WaitCommand(5.0).configure {
        priority = Command.Priority.LOWEST
        name = "MyWaitCommand"
        onHigherConflict = OnHigherConflict.CANCEL
        onEqualConflict = OnEqualConflict.QUEUE
    }
}

fun statementCommandConfigurationSample() {
    val cmd = WaitCommand(5.0)
    cmd.configure {
        priority = Command.Priority.LOWEST
        name = "MyWaitCommand"
        onHigherConflict = OnHigherConflict.CANCEL
        onEqualConflict = OnEqualConflict.QUEUE
    }
}

fun implicitCommandSchedulerSample() {
    val cmd = WaitCommand(5.0)

    // schedule implicitly links to DuckyScheduler's companion object
    cmd.schedule()
}

fun explicitCommandSchedulerSample(cs: CommandScheduler) {
    val cmd = WaitCommand(5.0)
    with(cs) {
        // schedule links to `cs` object
        cmd.schedule()
    }
}

fun triggerOnceOnSample() {
    ({ boolean1 }).onceOnTrue { println("hi") }
    ({ boolean1 }).onceOnTrue(WaitCommand(5.0))
}

fun triggerWhileOnSample() {
    ({ boolean1 }).whileOnTrue({ println("rising edge") }) { println("falling edge") }
    ({ boolean1 }).whileOnTrue(WaitCommand(5.0))
}

fun triggerCombinationSample() {
    ({ boolean1 && boolean2 }) // etc.
    // same as
    ({ boolean1 } and { boolean2 }) // etc.
}

fun triggerInversionSample() {
    val boolean1trigger = ({ boolean1 })

    // this will activate on falling edge instead of rising now
    (!boolean1trigger).onceOnTrue { println("hi") }
}

val boolean1 = true
val boolean2 = true
