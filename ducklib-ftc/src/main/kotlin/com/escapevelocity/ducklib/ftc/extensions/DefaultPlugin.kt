package com.escapevelocity.ducklib.ftc.extensions

import com.bylazar.ftcontrol.panels.plugins.BasePluginConfig
import com.bylazar.ftcontrol.panels.plugins.ModContext
import com.bylazar.ftcontrol.panels.plugins.PanelsPlugin
import com.qualcomm.ftccommon.FtcEventLoop

abstract class DefaultPlugin<T: BasePluginConfig>(baseConfig: T) : PanelsPlugin<T>(baseConfig) {
    override val globalVariables = mutableMapOf<String, () -> Any>()

    override val actions = mutableMapOf<String, () -> Unit>()

    override fun onRegister(context: ModContext) {}

    override fun onEnable() {}

    override fun onDisable() {}

    override fun onAttachEventLoop(eventLoop: FtcEventLoop) {}

}