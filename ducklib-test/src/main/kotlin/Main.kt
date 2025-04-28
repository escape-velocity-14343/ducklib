
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.escapevelocity.ducklib.command.commands.WaitCommand
import com.escapevelocity.ducklib.command.commands.setPriority
import com.escapevelocity.ducklib.command.scheduler.DuckyScheduler
import com.escapevelocity.ducklib.command.scheduler.DuckyScheduler.Scheduler.trigger
import com.escapevelocity.ducklib.geometry.Vector2
import kotlinx.coroutines.delay

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    var trigger1 by remember { mutableStateOf(false) }
    var trigger2 by remember { mutableStateOf(false) }
    var trigger3 by remember { mutableStateOf(false) }
    var trigger4 by remember { mutableStateOf(false) }
    var trigger5 by remember { mutableStateOf(false) }

    val v = Vector2(0.5, 1.0)
    val v2 = v.yx

    remember {
        val ss1 = Test1Subsystem()
        val ss2 = Test2Subsystem()
        val c1 = WaitCommand(5.0).setPriority(1)
        val c2 = WaitCommand(5.0).setPriority(2)
        val c3 = WaitCommand(5.0).setPriority(3)
        val c4 = WaitCommand(5.0).setPriority(4)
        val c5 = WaitCommand(5.0).setPriority(4)
        c1.addRequirements(ss1)
        c2.addRequirements(ss1)
        c3.addRequirements(ss1)
        c4.addRequirements(ss1)
        c5.addRequirements(ss1)

        ({ trigger1 }).trigger.onceOnTrue(c1)
        ({ trigger2 }).trigger.onceOnTrue(c2)
        ({ trigger3 }).trigger.onceOnTrue(c3)
        ({ trigger4 }).trigger.onceOnTrue(c4)
        ({ trigger5 }).trigger.onceOnTrue(c5)
        DuckyScheduler.addSubsystem(ss1)
        DuckyScheduler.addSubsystem(ss2)
        //DuckyScheduler.setDefaultCommand(ss1, c1)
        //c1.schedule()
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(33L)
            //delay(200L)
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
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.background(Color.Red).defaultMinSize(Dp(50.0F), Dp(50.0F)).pointerInput(Unit) {
                        detectTapGestures(onPress = {
                            trigger3 = true
                            tryAwaitRelease()
                            trigger3 = false
                        })
                    }) {
                    Text(trigger3.toString())
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.background(Color.Red).defaultMinSize(Dp(50.0F), Dp(50.0F)).pointerInput(Unit) {
                        detectTapGestures(onPress = {
                            trigger4 = true
                            tryAwaitRelease()
                            trigger4 = false
                        })
                    }) {
                    Text(trigger4.toString())
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.background(Color.Red).defaultMinSize(Dp(50.0F), Dp(50.0F)).pointerInput(Unit) {
                        detectTapGestures(onPress = {
                            trigger5 = true
                            tryAwaitRelease()
                            trigger5 = false
                        })
                    }) {
                    Text(trigger5.toString())
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
