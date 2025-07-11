package com.escapevelocity.ducklib.core.command.scheduler

import com.escapevelocity.ducklib.core.command.commands.Command
import com.escapevelocity.ducklib.core.command.commands.OnEqualConflict
import com.escapevelocity.ducklib.core.command.commands.OnHigherConflict
import com.escapevelocity.ducklib.core.command.subsystem.Subsystem
import com.escapevelocity.ducklib.core.util.b16Hash
import com.escapevelocity.ducklib.core.util.containsAny
import java.util.*

/**
 * The default ducklib scheduler.
 * Does both the jobs of [CommandScheduler] and [TriggerScheduler].
 *
 * If you want thread safety for some reason,
 * give each thread a new instance of DuckyScheduler and use it inside a [with] block.
 */
open class DuckyScheduler : CommandScheduler, TriggerScheduler {
    override val hasCommands
        get() = scheduledCommands.isNotEmpty()
    override val commands: Collection<Command>
        get() = scheduledCommands
    override val subsystems: Set<Subsystem>
        get() = _subsystems

    protected val scheduledCommands = HashSet<Command>()
    private val initializedCommands = HashSet<Command>()
    private val firstScheduleAttemptTime = HashMap<Command, Long>()
    private val queuedCommands = PriorityQueue<Command> { o1, o2 ->
        if (o1.priority == o2.priority)
            firstScheduleAttemptTime[o1]!!.compareTo(firstScheduleAttemptTime[o2]!!)
        else
            o1.priority.compareTo(o2.priority)
    }
    private val commandRequirements = HashMap<Any, Command>()
    private val _subsystems = HashSet<Subsystem>()

    private var deferLock = false
    private val deferredActions = ArrayList<() -> Unit>()

    private val triggers = ArrayList<TriggeredAction>()
    internal val lastTriggerValues = HashMap<TriggeredAction, Boolean?>()
    internal val lastTriggerNanos = HashMap<TriggeredAction, Long?>()

    override fun scheduleCommand(command: Command) {
        if (command.composed) throw kotlin.IllegalArgumentException("Composed command $command cannot be scheduled by itself")

        if (deferLock) {
            defer {
                scheduleCommand(command)
            }
            return
        }

        firstScheduleAttemptTime.putIfAbsent(command, System.nanoTime())

        if (command in scheduledCommands) {
            return
        }

        if (handleConflicts(command)) {
            // it could handle the conflicts!
            initOrResumeCommand(command)
            defer { queuedCommands.remove(command) }
            return
        }

        // it could not. 😢
        when (command.onHigherConflict) {
            OnHigherConflict.CANCEL -> defer {
                queuedCommands.remove(command)
                firstScheduleAttemptTime.remove(command)
            }

            OnHigherConflict.QUEUE -> defer {
                if (command !in queuedCommands) queuedCommands.add(command)
            }
        }
    }

    /**
     * @return Whether the conflicts have been "dealt with," or whether the command can be scheduled normally
     */
    private fun handleConflicts(command: Command): Boolean {
        val conflictingCommands = HashSet<Command>()
        for (requirement in command.requirements) {
            commandRequirements[requirement]?.let { conflictingCommands.add(it) }
        }

        if (conflictingCommands.isEmpty()) {
            // no conflicting commands
            return true
        }

        val highestConflict = conflictingCommands.maxByOrNull { it.priority }!!
        if (command.priority < highestConflict.priority) {
            // command has a lower or equal priority than the current highest; do nothing
            return false
        }

        if (command.priority == highestConflict.priority) {
            if (command.onEqualConflict == OnEqualConflict.QUEUE || firstScheduleAttemptTime[command]!! < firstScheduleAttemptTime[highestConflict]!!) {
                // command has equal priority to the highest but was scheduled later
                return false
            }
        }

        for (conflict in conflictingCommands) {
            if (conflict.suspendable) {
                defer {
                    suspendCommand(conflict)
                }
            } else {
                // no need to defer this because cancel already has its own deferring logic
                cancelCommand(conflict)
            }
        }

        return true
    }

    override fun cancelCommand(command: Command) {
        if (deferLock) {
            defer {
                cancelCommand(command)
            }
            return
        }

        if (command in queuedCommands) {
            queuedCommands.remove(command)
            initializedCommands.remove(command)
            return
        }

        if (command !in scheduledCommands) {
            return
        }

        command.end(true)
        removeCommand(command)
    }

    override fun bind(trigger: () -> Boolean, originalTrigger: (() -> Boolean)?, action: () -> Unit) {
        val ta = TriggeredAction(trigger, originalTrigger, action)
        triggers.add(ta)
        lastTriggerNanos[ta] = null
        lastTriggerValues[ta] = null
    }

    /**
     * Execute a single tick of the scheduler.
     *
     * This updates all the commands, including scheduling queued commands, rescheduling unrequired and unscheduled
     * subsystem commands, and updating trigger states.
     */
    override fun run() {
        deferLock = true
        try {
            for (command in scheduledCommands) {
                command.execute()
                if (command.finished) {
                    command.end(false)
                    defer {
                        command.remove()
                    }
                }
            }

            for (ta in triggers) {
                val triggerVal = ta.trigger()
                if (triggerVal) {
                    ta.action()
                    lastTriggerNanos[ta] = System.nanoTime()
                }
                lastTriggerValues[ta] = triggerVal
            }

            for (command in queuedCommands) command.schedule()
        } finally {
            deferLock = false
        }

        deferredActions.forEach { it() }
        deferredActions.clear()

        subsystems.forEach(Subsystem::periodic)
    }

    override fun addSubsystem(vararg subsystems: Subsystem) {
        _subsystems.addAll(subsystems)
    }

    override fun removeSubsystem(vararg subsystems: Subsystem) {
        _subsystems.removeAll(subsystems)
    }

    /**
     * Reset the state of the scheduler
     */
    override fun reset() {
        if (deferLock) {
            throw ConcurrentModificationException("Hey! Please do not reset the scheduler while it is running!")
        }

        scheduledCommands.clear()
        queuedCommands.clear()
        deferredActions.clear()
        commandRequirements.clear()
        _subsystems.clear()
        triggers.clear()
        lastTriggerNanos.clear()
        lastTriggerValues.clear()
    }

    private fun removeCommand(command: Command) {
        scheduledCommands.remove(command)
        commandRequirements.keys.removeAll(command.requirements)
        initializedCommands.remove(command)
        firstScheduleAttemptTime.remove(command)
    }

    private fun initOrResumeCommand(command: Command) {
        if (command in initializedCommands) {
            resumeCommand(command)
            return
        }

        scheduledCommands.add(command)
        for (subsystem in command.requirements) commandRequirements[subsystem] = command
        command.initialize()
        initializedCommands.add(command)
    }

    /**
     * Suspends a command while also handling removing the requirements
     */
    private fun suspendCommand(command: Command) {
        command.suspend()
        scheduledCommands.remove(command)
        queuedCommands.add(command)
        commandRequirements.keys.removeAll(command.requirements)
    }

    /**
     * Resumes a command while also handling adding back the requirements
     */
    private fun resumeCommand(command: Command) {
        command.resume()
        scheduledCommands.add(command)
        for (subsystem in command.requirements) commandRequirements[subsystem] = command
    }

    private fun defer(action: () -> Unit) {
        if (deferLock) {
            deferredActions.add(action)
        } else {
            action()
        }
    }

    private fun deferAlways(action: () -> Unit) {
        deferredActions.add(action)
    }

    private fun Command.remove() {
        removeCommand(this)
    }

    override val Command.conflicts
        get() = commandRequirements.keys.containsAny(this.requirements)

    override val Subsystem.command
        get() = commandRequirements.getOrDefault(this, null)

    override fun toString(): String {
        return """
Triggers:
${
            triggers.joinToString("\n").prependIndent()
        }
Registered subsystems:
${
            subsystems.joinToString(
                "\n",
            ) { "${if (it in commandRequirements) "×" else " "}${it.name}" }.prependIndent()
        }
Scheduled commands:
${scheduledCommands.joinToString("\n").prependIndent()}
Queued commands:
${queuedCommands.mapIndexed { i, cmd -> "$i (${cmd.priority}): $cmd" }.joinToString("\n").prependIndent()}
"""
    }

    data class TriggeredAction(
        val trigger: () -> Boolean,
        val originalTrigger: (() -> Boolean)? = null,
        val action: () -> Unit,
    ) {
        override fun toString(): String {
            val sb = StringBuilder()
            if (originalTrigger != null) {
                sb.append(originalTrigger)
            } else {
                sb.append("Trigger@")
                sb.append(b16Hash())
            }
            val lastTriggerValue = lastTriggerValues[this]
            if (lastTriggerValue != null) {
                sb.append(" [")
                sb.append(if (lastTriggerValue) "x" else " ")
                sb.append("]")
            }
            val lt = lastTriggerNanos[this]
            if (lt != null) {
                sb.append(" (%.3f)".format((System.nanoTime() - lt) / 1e9))
            }
            return sb.toString()
        }
    }

    /**
     * Schedule a command on the rising edge of the boolean supplier.
     *
     * @param command The command to schedule
     * @return The same boolean supplier for chaining calls
     */
    fun <T : () -> Boolean> T.onceOnTrue(command: Command): T = onceOnTrue { command.schedule() }

    /**
     * Evaluate the command supplier, then schedule the result on the rising edge of the boolean supplier.
     * This is similar to a [com.escapevelocity.ducklib.core.command.commands.composition.DeferredCommand],
     * but it's more flexible.
     *
     * @param command The command supplier to evaluate on the rising edge
     * @return The same boolean supplier for chaining calls
     */
    fun <T : () -> Boolean> T.onceOnTrueDefer(command: () -> Command): T = onceOnTrue { command().schedule() }

    /**
     * Schedule a command on the falling edge of the boolean supplier.
     *
     * @param command The command to schedule
     */
    fun <T : () -> Boolean> T.onceOnFalse(command: Command): T = onceOnFalse { command.schedule() }

    /**
     * Evaluate the command supplier, then schedule the result on the falling edge of the boolean supplier.
     * This is similar to a [com.escapevelocity.ducklib.core.command.commands.composition.DeferredCommand],
     * but it's more flexible.
     *
     * @param command The command supplier to evaluate on the rising edge
     * @return The same boolean supplier for chaining calls
     */
    fun <T : () -> Boolean> T.onceOnFalseDefer(command: () -> Command): T = onceOnFalse { command().schedule() }

    /**
     * Schedule a command on the falling edge of the boolean supplier,
     * then cancel it on the falling edge.
     *
     * @param command The command to schedule
     * @return The same boolean supplier for chaining calls
     */
    fun <T : () -> Boolean> T.whileOnTrue(command: Command) =
        onceOnTrue { command.schedule() }.onceOnFalse { command.cancel() }

    fun <T : () -> Boolean> T.whileOnTrueDefer(command: () -> Command): T {
        var lastVal = this()
        lateinit var realCmd: Command
        bind({
            val thisVal = this()
            val ret = thisVal && !lastVal
            lastVal = thisVal
            ret
        }, this) {
            realCmd = command()
            realCmd.schedule()
        }
        bind({
            val thisVal = this()
            val ret = !thisVal && lastVal
            lastVal = thisVal
            ret
        }, this) {
            realCmd.cancel()
        }
        return this
    }

    companion object : DuckyScheduler() {
    }
}