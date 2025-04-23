import com.escapevelocity.ducklib.command.commands.*
import com.escapevelocity.ducklib.command.commands.Command.Scheduler.scheduleCommand
import com.escapevelocity.ducklib.command.commands.group.SequentialCommandGroup

fun main() {
    val c = SequentialCommandGroup(
        InstantCommand { println("aaa") },
        WaitCommand(1.0),
        InstantCommand { println("fish") },
    ).alongWith(InstantCommand { println("hi") })

    c.scheduleCommand()
    while (true) Command.run()
}