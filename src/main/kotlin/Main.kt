import com.escapevelocity.ducklib.command.commands.Command
import com.escapevelocity.ducklib.command.commands.Command.Scheduler.scheduleCommand
import com.escapevelocity.ducklib.command.commands.InstantCommand

fun main() {
    val c = InstantCommand { println("hi") };
    c.scheduleCommand()
    Command.run()
}