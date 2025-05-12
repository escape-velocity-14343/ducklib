package com.escapevelocity.ducklib.core.test

import com.escapevelocity.ducklib.core.command.commands.InstantCommand
import com.escapevelocity.ducklib.core.command.commands.NoOpCommand
import com.escapevelocity.ducklib.core.command.commands.composition.forever
import com.escapevelocity.ducklib.core.command.commands.composition.repeat
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler
import com.escapevelocity.ducklib.core.util.deadlines
import com.escapevelocity.ducklib.core.util.races
import com.escapevelocity.ducklib.core.util.then
import com.escapevelocity.ducklib.core.util.with
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class SchedulerTest {
    @Test
    fun testCommandGroupComposed() {
        with(DuckyScheduler()) {
            val cmd1 = InstantCommand { }
            val cmd2 = InstantCommand { }
            val group = cmd1 with cmd2
            // should fail because `group` composes cmd1 already
            assertFails { cmd1.schedule() }
        }
    }

    @Test
    fun testParallel() {
        with(DuckyScheduler()) {
            var a = 0
            var b = 0
            val group = InstantCommand { a = 1 } with InstantCommand { b = 1 }
            group.schedule()
            // neither has been set yet
            assertEquals(0, a)
            assertEquals(0, b)
            this.run()
            // both should be set
            assertEquals(1, a)
            assertEquals(1, b)
        }
    }

    @Test
    fun testSequential() {
        with(DuckyScheduler()) {
            var a = 0
            var b = 0
            val group = InstantCommand { a = 1 } then InstantCommand { b = 1 }
            group.schedule()
            assertEquals(0, a)
            assertEquals(0, b)
            this.run()
            // after the first step, `a` is set but `b` isn't
            assertEquals(1, a)
            assertEquals(0, b)
            this.run()
            // after the second step, `a` and `b` are both set
            assertEquals(1, b)
        }
    }

    @Test
    fun testRace() {
        with(DuckyScheduler()) {
            var a = 0
            var b = 0
            val group = NoOpCommand() then InstantCommand { a = 1 } races InstantCommand { b = 1 }
            group.schedule()
            assertEquals(0, a)
            assertEquals(0, b)
            this.run()
            // after the first step, `b` is set but `a` isn't since it's one tick behind
            assertEquals(0, a)
            assertEquals(1, b)
            this.run()
            // after the second step, `b` is set and the command group has finished because it's a race group
            assertEquals(0, a)
            assertEquals(1, b)
        }
    }

    @Test
    fun testDeadline() {
        with(DuckyScheduler()) {
            var a = 0
            var b = 0
            val group = NoOpCommand() deadlines (InstantCommand { a = 1 } then InstantCommand { b = 1 })
            group.schedule()
            assertEquals(0, a)
            assertEquals(0, b)
            this.run()
            assertEquals(1, a)
            assertEquals(0, b)
            this.run()
            assertEquals(1, a)
            assertEquals(0, b)
        }
    }

    @Test
    fun testLoopInfinite() {
        with(DuckyScheduler()) {
            var a = 0
            val group = InstantCommand { a++ }.forever()
            group.schedule()
            for (i in 1..10) {
                run()
            }
            assertEquals(10, a)
        }
    }

    @Test
    fun testLoopFinite() {
        with(DuckyScheduler()) {
            var a = 0
            val group = InstantCommand { a++ }.repeat(5)
            group.schedule()
            for (i in 1..10) {
                run()
            }
            assertEquals(5, a)
        }
    }
}