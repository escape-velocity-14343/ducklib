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
import com.escapevelocity.ducklib.core.command.commands.OnEqualConflict
import com.escapevelocity.ducklib.core.command.commands.WaitCommand
import com.escapevelocity.ducklib.core.command.commands.configure
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler.Companion.onceOnTrue
import com.escapevelocity.ducklib.core.geometry.Vector2
import com.escapevelocity.ducklib.core.geometry.div
import com.escapevelocity.ducklib.core.geometry.inches
import com.escapevelocity.ducklib.core.geometry.radians
import kotlinx.coroutines.delay
import kotlin.math.PI

@Composable
@Preview
fun App() {
    println("app run")
    var text by remember { mutableStateOf("Hello, World!") }
    var trigger1 by remember { mutableStateOf(false) }
    var trigger2 by remember { mutableStateOf(false) }
    var trigger3 by remember { mutableStateOf(false) }
    var trigger4 by remember { mutableStateOf(false) }
    var trigger5 by remember { mutableStateOf(false) }

    val v = Vector2(0.5.inches, 1.0.inches)
    val v2 = v.yx
    val v3 = Vector2.fromAngle(PI / 2.0.radians)

    remember {
        val ss1 = Test1Subsystem()
        val ss2 = Test2Subsystem()
        val c1 = WaitCommand(2.0).configure {
            priority = 1
            onEqualConflict = OnEqualConflict.OVERRIDE
        }
        val c2 = WaitCommand(2.0).configure {
            priority = 1
            onEqualConflict = OnEqualConflict.QUEUE
        }
        val c3 = WaitCommand(2.0).configure {
            priority = 2
            onEqualConflict = OnEqualConflict.OVERRIDE
        }
        val c4 = WaitCommand(2.0).configure {
            priority = 1
            onEqualConflict = OnEqualConflict.OVERRIDE
        }
        val c5 = WaitCommand(2.0).configure {
            priority = 4
            onEqualConflict = OnEqualConflict.OVERRIDE
        }

        //val g = c1 then c2 then c3 with c4
        //ParallelCommandGroup(SequentialCommandGroup(c1, c2, c3), c4)

        c1.addRequirements(ss1)
        c2.addRequirements(ss1)
        c3.addRequirements(ss1, ss2)
        c4.addRequirements(ss2)
        c5.addRequirements(ss1)

        ({ trigger1 }).onceOnTrue(c1)
        ({ trigger2 }).onceOnTrue(c2)
        ({ trigger3 }).onceOnTrue(c3)
        ({ trigger4 }).onceOnTrue(c4)
        ({ trigger5 }).onceOnTrue(c5)
        ({ trigger5 }).onceOnTrue(c5)
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
                TriggerButton(trigger1::toString) { trigger1 = it }
                TriggerButton(trigger2::toString) { trigger2 = it }
                TriggerButton(trigger3::toString) { trigger3 = it }
                TriggerButton(trigger4::toString) { trigger4 = it }
                TriggerButton(trigger5::toString) { trigger5 = it }
            }
            Text(text)
        }
    }
}

@Composable
fun TriggerButton(text: () -> String, onPressChanged: (Boolean) -> Unit) = Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier.background(Color.Red).defaultMinSize(Dp(50.0F), Dp(50.0F)).pointerInput(Unit) {
        detectTapGestures(onPress = {
            onPressChanged(true)
            tryAwaitRelease()
            onPressChanged(false)
        })
    }) {
    Text(text())
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
