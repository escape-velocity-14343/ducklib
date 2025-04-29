package com.escapevelocity.ducklib.command.commands

class DeferredCommand(val commandSupplier: () -> Command, vararg requirements: Any) : Command() {
    init {
        addRequirements(requirements)
    }

    private var cmd: Command? = null

    override val suspendable: Boolean
        get() = cmd?.suspendable ?: false

    override fun initialize() {
        cmd = commandSupplier()
        cmd?.initialize()
    }

    override fun execute() {
        cmd?.execute()
    }

    override val finished: Boolean
        get() = cmd?.finished == true

    override fun suspend() {
        cmd?.suspend()
    }

    override fun resume() {
        cmd?.resume()
    }

    override fun end(interrupted: Boolean) {
        cmd?.end(interrupted)
    }
}