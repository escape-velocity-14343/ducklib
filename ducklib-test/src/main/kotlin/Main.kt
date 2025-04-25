import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.escapevelocity.ducklib.command.commands.*
import com.escapevelocity.ducklib.command.scheduler.DuckyScheduler
import com.escapevelocity.ducklib.command.scheduler.DuckyScheduler.Scheduler.schedule
import kotlinx.coroutines.delay

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    val c = WaitCommand(1.0) with WaitCommand(1.0) then WaitCommand(1.0)

    LaunchedEffect(Unit) {
        while (true) {
            delay(33L)
            DuckyScheduler.run()
            text = DuckyScheduler.toString()
        }
    }

    MaterialTheme {
        Column {
            Text(text)
            Button(onClick = {
                c.schedule()
            }) {}
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
