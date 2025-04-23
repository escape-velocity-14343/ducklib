import com.escapevelocity.ducklib.command.commands.InstantCommand
import com.escapevelocity.ducklib.command.commands.WaitCommand
import com.escapevelocity.ducklib.command.commands.alongWith
import com.escapevelocity.ducklib.command.commands.group.SequentialCommandGroup
import com.escapevelocity.ducklib.command.scheduler.DuckyScheduler

fun main() {
    val ds = DuckyScheduler()
    val c = SequentialCommandGroup(
        InstantCommand { println("aaa") },
        WaitCommand(1.0),
        InstantCommand { println("fish") },
    ).alongWith(InstantCommand { println("hi") })

//    { System.`in`. }

    ds.schedule(c)
    while (true) ds.run()
}