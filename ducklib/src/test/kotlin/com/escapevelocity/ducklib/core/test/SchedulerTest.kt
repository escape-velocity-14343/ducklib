package com.escapevelocity.ducklib.core.test

import com.escapevelocity.ducklib.core.command.commands.OnEqualConflict
import com.escapevelocity.ducklib.core.command.commands.composition.forever
import com.escapevelocity.ducklib.core.command.commands.configure
import com.escapevelocity.ducklib.core.command.commands.instant
import com.escapevelocity.ducklib.core.command.commands.priority
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler
import com.escapevelocity.ducklib.core.command.subsystem.Requirement
import kotlin.test.Test
import kotlin.test.assertEquals

class SchedulerTest {
    @Test
    fun testConflictLowerPriority() {
        with(DuckyScheduler()) {
            val lock = Requirement()

            var a = -1
            val cmd1 = { a = 0 }.instant(lock).forever.configure { priority = 1.priority }
            val cmd2 = { a += 1 }.instant(lock).forever.configure { priority = 0.priority }
            cmd1.schedule()
            run()
            run()
            run()
            assertEquals(0, a)
            cmd2.schedule()
            run()
            run()
            run()
            assertEquals(0, a)
        }
    }

    @Test
    fun testConflictEqualPriorityQueue() {
        with(DuckyScheduler()) {
            val lock = Requirement()

            var a = -1
            val cmd1 = { a = 0 }.instant(lock).forever
            val cmd2 = { a += 1 }.instant(lock).forever.configure {
                onEqualConflict = OnEqualConflict.QUEUE
            }
            cmd1.schedule()
            run()
            run()
            run()
            assertEquals(0, a)
            cmd2.schedule()
            run()
            run()
            run()
            assertEquals(0, a)
        }
    }

    @Test
    fun testConflictEqualPriorityOverride() {
        with(DuckyScheduler()) {
            val lock = Requirement() as Any

            var a = -1
            val cmd1 = { a = 0 }.instant(lock).forever
            val cmd2 = { a += 1 }.instant(lock).forever.configure {
                onEqualConflict = OnEqualConflict.OVERRIDE
            }
            assertEquals(cmd1.requirements.first(), lock)
            assertEquals(cmd1.requirements.first(), cmd2.requirements.first())
            cmd1.schedule()
            run()
            run()
            run()
            assertEquals(0, a)
            cmd2.schedule()
            run()
            run()
            run()
            assertEquals(3, a)
        }
    }

    @Test
    fun testConflictHigherPriority() {
        with(DuckyScheduler()) {
            val lock = Requirement() as Any

            var a = -1
            val cmd1 = { a = 0 }.instant(lock).forever
            val cmd2 = { a += 1 }.instant(lock).forever.configure {
                priority = cmd1.priority + 1.priority
            }
            assertEquals(cmd1.requirements.first(), lock)
            assertEquals(cmd1.requirements.first(), cmd2.requirements.first())
            cmd1.schedule()
            run()
            run()
            run()
            assertEquals(0, a)
            cmd2.schedule()
            run()
            run()
            run()
            assertEquals(3, a)
        }
    }
}