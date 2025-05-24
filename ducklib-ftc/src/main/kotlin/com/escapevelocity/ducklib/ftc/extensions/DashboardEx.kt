package com.escapevelocity.ducklib.ftc.extensions

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.ValueProvider
import com.escapevelocity.ducklib.core.util.ValDelegate
import kotlin.reflect.KProperty

object DashboardEx {
    val dash
        get() = FtcDashboard.getInstance() as FtcDashboard

    val variables = HashMap<String, Any?>()

    /**
     * Adds a variable to the FtcDashboard dashboard and binds it to this variable with a property delegate.
     * Specify a category to put the property in with a slash-separated component like `category/name`.
     *
     * Use it like
     * ```
     * val configVar by DashboardEx["configVar", 0.5]
     * ```
     * The return type will be inferred from the initial type.
     */
    inline operator fun <reified T : Any> get(property: String, initial: T): ValDelegate<T> {
        val (category, name) = convertProperty(property)
        variables[property] = initial
        // add the config variable to the thing
        dash.addConfigVariable(
            category, name,
            object : ValueProvider<T> {
                override fun get() = variables[property] as T

                override fun set(value: T?) {
                    variables[property] = value
                }
            }
        )

        return object : ValDelegate<T> {
            override operator fun getValue(thisRef: Any?, property: KProperty<*>) = dashProperty

            // ?????
            private inline val dashProperty
                get() = variables[property] as T
        }
    }

    /**
     * Removes all the config variables from the Dashboard and from the variable storage map.
     */
    fun removeConfigVariables() {
        //variables.entries
        //    .map { (name, _) -> convertProperty(name) }
        //    .forEach { (category, name) ->
        //        dash.removeConfigVariable(category, name)
        //    }
        variables.clear()
    }

    fun convertProperty(property: String): Pair<String, String> {
        val propertySplit = property.split("/")
        return (if('/' in property) propertySplit[0] else "Config") to propertySplit.last()
    }

    override fun toString() =
        "${super.toString()} {\n${variables.entries.joinToString("\n") { (name, value) -> "$name: $value" }}\n}"
}