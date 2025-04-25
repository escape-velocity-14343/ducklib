import com.escapevelocity.ducklib.command.commands.*

fun main() {
    val c = InstantCommand {  } then WaitCommand(1.0) then InstantCommand {  } with InstantCommand {  } then InstantCommand {  }
    //val c = InstantCommand { println("hi") }.setName("hi") then (WaitCommand(1.0))
    c.setName("hi2")
    println(c)
    //val buf = System.`in`.buffered();

    //{ buf.read().toChar() == 'a' }.trigger(DuckyScheduler).onceOnTrue(InstantCommand { println("hi") })
    //c.schedule()

    //println(DuckyScheduler)
    //while (true) {
    //    DuckyScheduler.run()
    //}
}