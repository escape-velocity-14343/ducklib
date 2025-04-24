
import com.escapevelocity.ducklib.command.commands.InstantCommand
import com.escapevelocity.ducklib.command.commands.WaitCommand
import com.escapevelocity.ducklib.command.commands.then
import com.escapevelocity.ducklib.command.commands.with
import com.escapevelocity.ducklib.command.scheduler.DuckyScheduler
import com.escapevelocity.ducklib.command.trigger.trigger

fun main() {
    val c = InstantCommand { println("aaa") } then WaitCommand(1.0) then InstantCommand { println("fish") } with InstantCommand { println("hi") } then InstantCommand { println("hello") }

    val buf = System.`in`.buffered();

    { buf.read().toChar() == 'a' }.trigger(DuckyScheduler).onceOnTrue(InstantCommand { println("hi") })
//    c.schedule()

    while (true) DuckyScheduler.run()
}