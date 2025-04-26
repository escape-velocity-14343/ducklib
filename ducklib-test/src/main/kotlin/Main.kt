import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.escapevelocity.ducklib.command.commands.*
import com.escapevelocity.ducklib.command.scheduler.DuckyScheduler
import com.escapevelocity.ducklib.command.scheduler.DuckyScheduler.Scheduler.trigger
import kotlinx.coroutines.delay

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    var trigger1 by remember { mutableStateOf(false) }
    var trigger2 by remember { mutableStateOf(false) }

    remember {
        val ss1 = Test1Subsystem()
        val ss2 = Test2Subsystem()
        val c1 = WaitCommand(5.0) then WaitCommand(2.0) then WaitCommand(3.0) with WaitCommand(1.0) with WaitCommand(5.0) deadlineWith WaitCommand(0.5)
        val c2 = WaitCommand(1.0).setConflictResolution(Command.SubsystemConflictResolution.CANCEL_OTHER)
        c1.addRequirements(ss1)
        c2.addRequirements(ss1)

        ({ trigger1 }).trigger.onceOnTrue(c1)
        ({ trigger2 }).trigger.onceOnTrue(c2)
        DuckyScheduler.addSubsystem(ss1)
        DuckyScheduler.addSubsystem(ss2)
        DuckyScheduler.setDefaultCommand(ss1, c1)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(33L)
            DuckyScheduler.run()
            text = DuckyScheduler.toString()
        }
    }

    MaterialTheme(typography = Typography(FontFamily.Monospace), colors = MaterialTheme.colors.copy(isLight = false)) {
        Column {
            Row {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.background(Color.Red).defaultMinSize(Dp(50.0F), Dp(50.0F)).pointerInput(Unit) {
                        detectTapGestures(onPress = {
                            trigger1 = true
                            tryAwaitRelease()
                            trigger1 = false
                        })
                    }) {
                    Text(trigger1.toString())
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.background(Color.Red).defaultMinSize(Dp(50.0F), Dp(50.0F)).pointerInput(Unit) {
                        detectTapGestures(onPress = {
                            trigger2 = true
                            tryAwaitRelease()
                            trigger2 = false
                        })
                    }) {
                    Text(trigger2.toString())
                }
            }
            Text(text)
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
