import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material.Button
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
import com.escapevelocity.ducklib.command.commands.*
import com.escapevelocity.ducklib.command.scheduler.DuckyScheduler
import com.escapevelocity.ducklib.command.scheduler.DuckyScheduler.Scheduler.schedule
import com.escapevelocity.ducklib.command.trigger.trigger
import kotlinx.coroutines.delay

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    var buttonPressed by remember { mutableStateOf(false) }

    val c = WaitCommand(1.0) with WaitCommand(1.0) then WaitCommand(1.0);

    remember { { buttonPressed }.trigger(DuckyScheduler).onceOnTrue(WaitCommand(1.0)) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(33L)
            DuckyScheduler.run()
            text = DuckyScheduler.toString()
        }
    }

    MaterialTheme(typography = Typography(FontFamily.Monospace)) {
        Column {
            Button(onClick = {
                c.schedule()
            }) {}
            Box(contentAlignment = Alignment.Center, modifier = Modifier.background(Color.Red).defaultMinSize(Dp(50.0F), Dp(50.0F)).pointerInput(Unit) {
                detectTapGestures(onPress = {
                    buttonPressed = true
                    tryAwaitRelease()
                    buttonPressed = false
                })
            }) {
                Text(buttonPressed.toString())
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
