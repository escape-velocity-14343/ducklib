package com.escapevelocity.ducklib.core.test

import com.escapevelocity.ducklib.core.command.commands.InstantCommand
import com.escapevelocity.ducklib.core.command.commands.LambdaCommand
import com.escapevelocity.ducklib.core.command.commands.NoOpCommand
import com.escapevelocity.ducklib.core.command.commands.WaitCommand
import com.escapevelocity.ducklib.core.command.commands.composition.DeferredCommand
import com.escapevelocity.ducklib.core.command.commands.composition.IfCommand
import com.escapevelocity.ducklib.core.command.commands.composition.IfElseCommand
import com.escapevelocity.ducklib.core.command.commands.composition.WhenCommand
import com.escapevelocity.ducklib.core.command.commands.composition.forever
import com.escapevelocity.ducklib.core.command.commands.composition.repeat
import com.escapevelocity.ducklib.core.command.commands.configure
import com.escapevelocity.ducklib.core.command.commands.instant
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler
import com.escapevelocity.ducklib.core.util.deadlines
import com.escapevelocity.ducklib.core.util.races
import com.escapevelocity.ducklib.core.util.then
import com.escapevelocity.ducklib.core.util.with
import com.escapevelocity.ducklib.core.util.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.time.Duration.Companion.milliseconds

class CommandTest {
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
            val group = InstantCommand { a++ }.forever
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

    @Test
    fun testDeferredCommand() {
        with(DuckyScheduler()) {
            val cmd = DeferredCommand {
                LambdaCommand {
                    var a = 0
                    initialize = { a += 1 }
                    end = { assertEquals(1, a) }
                }
            }
            cmd.schedule()
            run()
            cmd.schedule()
            // doesn't fail here because it's an entirely new LambdaCommand, unlike in testLambdaCommand()
            run()
        }
    }

    @Test
    fun testLambdaCommand() {
        with(DuckyScheduler()) {
            val cmd = LambdaCommand {
                var a = 0
                initialize = { a += 1 }
                end = { assertEquals(1, a) }
            }
            cmd.schedule()
            // doesn't fail because a is 1
            run()
            cmd.schedule()
            // should fail because `a` equals 2 since it's the same command with internal state getting scheduled twice
            assertFails { run() }
        }
    }

    @Test
    fun testIfCommand() {
        with(DuckyScheduler()) {
            var cond = false
            var a = 0
            val cmd = IfCommand({ cond }, { a += 1 }.instant())
            cmd.schedule()
            run()
            assertEquals(0, a)
            cond = true
            cmd.schedule()
            run()
            assertEquals(1, a)
        }
    }

    @Test
    fun testIfElseCommand() {
        with(DuckyScheduler()) {
            var cond = false
            var a = 0
            val cmd = IfElseCommand({ cond }, { a += 1 }.instant(), { a += 2 }.instant())
            cmd.schedule()
            run()
            assertEquals(2, a)
            cond = true
            cmd.schedule()
            run()
            assertEquals(3, a)
        }
    }

    @Test
    fun testWhenCommand() {
        with(DuckyScheduler()) {
            var value = 0
            var a = ""
            val cmd = WhenCommand { value }.configure {
                this[0] = { a = "hi!" }.instant()
                this[1] = { a = "hello!" }.instant().forever
                this[2] = { a = "fish!" }.instant().forever
                this[3] = { a = "duck!" }.instant().forever
            }

            cmd.schedule()
            run()
            assertEquals("hi!", a)
            a = ""
            // now does nothing because that command should be descheduled
            run()
            assertEquals("", a)
            value = 2
            cmd.schedule()
            run()
            assertEquals("fish!", a)
            a = ""
            run()
            assertEquals("fish!", a)
        }
    }

    @Test
    fun testTimeoutCommand() {
        with(DuckyScheduler()) {
            var a = 0
            val cmd = { a += 1 }.instant().forever.withTimeout(100.milliseconds)
            cmd.schedule()
            run()
            assertEquals(1, a)
            run()
            assertEquals(2, a)
            Thread.sleep(150)
            run()
            assertEquals(3, a)
            run()
            assertEquals(3, a)
        }
    }

    @Test
    fun testWaitUntilCommand() {
        with(DuckyScheduler()) {
            var a = 0
            val cmd =
                { a += 1 }.instant() then WaitCommand(100.milliseconds) then { a += 1 }.instant()
            cmd.schedule()
            run()
            assertEquals(1, a)
            for (i in 1..10) {
                run()
                assertEquals(1, a)
            }
            Thread.sleep(150)
            run()
            run()
            assertEquals(2, a)
        }
    }
}