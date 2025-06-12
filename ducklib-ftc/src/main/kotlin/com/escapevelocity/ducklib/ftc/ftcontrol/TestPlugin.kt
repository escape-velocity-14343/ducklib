package com.escapevelocity.ducklib.ftc.ftcontrol

import com.bylazar.ftcontrol.panels.Panels
import com.bylazar.ftcontrol.panels.plugins.BasePluginConfig
import com.bylazar.ftcontrol.panels.plugins.ModContext
import com.bylazar.ftcontrol.panels.plugins.Page
import com.bylazar.ftcontrol.panels.plugins.PanelsPlugin
import com.bylazar.ftcontrol.panels.plugins.html.primitives.Text
import com.bylazar.ftcontrol.panels.plugins.html.primitives.div
import com.bylazar.ftcontrol.panels.plugins.html.primitives.dynamic
import com.bylazar.ftcontrol.panels.plugins.html.primitives.empty
import com.bylazar.ftcontrol.panels.plugins.html.primitives.p
import com.bylazar.ftcontrol.panels.plugins.html.primitives.text
import com.escapevelocity.ducklib.ftc.ftcontrol.DefaultPlugin
import com.qualcomm.ftccommon.FtcEventLoop

open class TestPluginConfig : BasePluginConfig() {
    //override var isDev = true
}

class TestPlugin : PanelsPlugin<TestPluginConfig>(TestPluginConfig()) {

    override val globalVariables: MutableMap<String, () -> Any> = mutableMapOf<String, () -> Any>(
        "time" to { System.currentTimeMillis() },
        "testVar" to { testVar }
    )

    override val actions: MutableMap<String, () -> Unit> = mutableMapOf<String, () -> Unit>(
        "test" to { testVar += 1.0 },
        "test2" to { testVar = 5.0 },
    )

    var testVar: Double = 0.0

    override fun onRegister(context: ModContext) {
        testVar = 0.0
        createPage(
            Page(
                id = "thisisaverycoolidthatisveryreal",
                title = "Test Page",
                html = div {
                    p {
                        text("time: ")
                        dynamic("time")
                    }
                    p {
                        dynamic("testVar")
                    }
                    button(action="test") {
                        text("Test")
                    }
                }
            )
        )
    }

    override var id: String = "com.escapevelocity.ducklib.ftc.ftcontrol.testplugin"

    override val name: String = "TestPlugin"

    override fun onEnable() {
    }

    override fun onDisable() {
    }

    override fun onAttachEventLoop(eventLoop: FtcEventLoop) {
    }

}