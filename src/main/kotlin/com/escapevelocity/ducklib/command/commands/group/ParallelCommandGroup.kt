package com.escapevelocity.ducklib.command.commands.group

import com.escapevelocity.ducklib.command.commands.Command

abstract class CommandGroup(vararg var commands: Command): Command() {
}