package com.escapevelocity.ducklib.ftc.extensions

import com.bylazar.ftcontrol.panels.Panels
import com.bylazar.ftcontrol.panels.plugins.BasePluginConfig
import com.bylazar.ftcontrol.panels.plugins.ModContext
import com.bylazar.ftcontrol.panels.plugins.PanelsPlugin

class TestPluginConfig : BasePluginConfig() {
    override var isDev = true
}

class TestPlugin : DefaultPlugin<TestPluginConfig>(TestPluginConfig()) {
    override val globalVariables = mutableMapOf<String, () -> Any>(
        "testVariable" to { 0 },
    )

    override val actions = mutableMapOf<String, () -> Unit>(
        "testAction" to { globalVariables["testVariable"] = { 1 }; println("Hello, World!") },

        "testAction2" to {
            globalVariables["testVariable"] = { 0 }
            Panels.getTelemetry().debug("Hello, World, with telem!")
            Panels.getTelemetry().update()
        },
    )

    override var id: String = "test"

    override val name: String = "TestPlugin"

}