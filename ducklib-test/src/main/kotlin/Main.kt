import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.escapevelocity.ducklib.core.command.commands.*
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler
import com.escapevelocity.ducklib.core.command.scheduler.DuckyScheduler.Companion.onceOnTrue
import com.escapevelocity.ducklib.core.geometry.*
import com.escapevelocity.ducklib.core.util.races
import com.escapevelocity.ducklib.core.util.with
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

val typography = Typography(defaultFontFamily = FontFamily.Monospace)

@Composable
@Preview
fun SchedulerTestApp() {
    var text by remember { mutableStateOf("Hello, World!") }
    var trigger1 by remember { mutableStateOf(false) }
    var trigger2 by remember { mutableStateOf(false) }
    var trigger3 by remember { mutableStateOf(false) }
    var trigger4 by remember { mutableStateOf(false) }
    var trigger5 by remember { mutableStateOf(false) }

    remember {
        val ss1 = Test1Subsystem()
        val ss2 = Test2Subsystem()
        val c1 = WaitCommand(2.seconds).configure {
            priority = 1.priority
            onEqualConflict = OnEqualConflict.OVERRIDE
            addRequirements(ss1)
        }
        val c2 = WaitCommand(2.seconds).configure {
            priority = 1.priority
            onEqualConflict = OnEqualConflict.QUEUE
            addRequirements(ss1)
        }
        val c3 = WaitCommand(2.seconds).configure {
            priority = 2.priority
            onEqualConflict = OnEqualConflict.OVERRIDE
            addRequirements(ss1, ss2)
        }
        val c4 = WaitCommand(2.seconds) races WaitCommand(1.seconds) with WaitCommand(1.5.seconds)
        val c5 = LambdaCommand {

            var startTime = 0L
            suspendable = true
            initialize = {
                startTime = System.nanoTime()
            }
            finished = { (System.nanoTime() - startTime) / 1e9 > 5.0 }
            end = { println("hi") }
            config = {
                priority = 1.priority
                addRequirements(ss1)
            }
        }
        val lmCommand = LambdaCommand {
            var state = 0.0
            suspendable = true
            execute = {
                state += 1.0
            }
            end = {
                println("finished")
            }
        }

        ({ trigger1 }).onceOnTrue(c1)
        ({ trigger2 }).onceOnTrue(c2)
        ({ trigger3 }).onceOnTrue(c3)
        ({ trigger4 }).onceOnTrue(c4)
        ({ trigger5 }).onceOnTrue(c5)
        ({ trigger5 }).onceOnTrue(c5)
        DuckyScheduler.addSubsystem(ss1)
        DuckyScheduler.addSubsystem(ss2)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(33L)
            DuckyScheduler.run()
            text = DuckyScheduler.toString()
        }
    }

    MaterialTheme(typography = typography) {
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
@Preview
fun GeometryTestApp() {
    var angle1 by remember { mutableStateOf(0.0.radians) }
    var angle2 by remember { mutableStateOf(0.0.radians) }

    MaterialTheme(typography = typography) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            RotatingWidget(Modifier.size(200.dp)) {
                angle1 = round(it, Radians.fromRotations(0.125))
                angle1
            }
            RotatingWidget(Modifier.size(200.dp)) {
                angle2 = round(it, Radians.fromRotations(0.125))
                angle2
            }
            Text("%.1s".format(angle1.angleTo(angle2)))
        }
    }
}

@Composable
fun TriggerButton(text: () -> String, onPressChanged: (Boolean) -> Unit) = Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier.background(Color.Red).defaultMinSize(50.dp, 50.dp).pointerInput(Unit) {
        detectTapGestures(onPress = {
            onPressChanged(true)
            tryAwaitRelease()
            onPressChanged(false)
        })
    }) {
    Text(text())
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RotatingWidget(modifier: Modifier = Modifier, onAngleChanged: (Radians) -> Radians) {
    var angle by remember { mutableStateOf(0.0.radians) }
    var totalDrag by remember { mutableStateOf(Vector2.Y) }
    var textSize by remember { mutableStateOf(IntSize(0, 0)) }
    Box(
        modifier = modifier.background(Color.LightGray, CircleShape).pointerInput(Unit) {
            detectDragGestures(onDragStart = {
                totalDrag = Vector2(it.x.inches, it.y.inches) - Vector2(size.width.inches, size.height.inches) * 0.5
            }, onDrag = {
                totalDrag += Vector2(it.x.inches, it.y.inches)
                angle = onAngleChanged((totalDrag).angle)
            })
        }, contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val x = center.x + (cos(angle) * 0.5 * (size.width - 20)).dp.toPx()
            val y = center.y + (sin(angle) * 0.5 * (size.height - 20)).dp.toPx()
            //drawCircle(Color.Gray, radius = 5.dp.toPx(), center = Offset(x, y))
            drawLine(Color.Gray, center, Offset(x, y), 5.0F, cap = StrokeCap.Round)
            //val (rectX, rectY) = textSize
            //drawRoundRect(Color.LightGray, topLeft = Offset(-rectX * 0.5F, -rectY * 0.5F))
        }
        Text("%.1s".format(angle), onTextLayout = {
            textSize = it.size
        })
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        SchedulerTestApp()
    }

    //Window(onCloseRequest = ::exitApplication) {
    //    GeometryTestApp()
    //}
}
