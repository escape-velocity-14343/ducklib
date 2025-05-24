# FTC Dashboard integration

If [FTC dashboard](https://github.com/acmerobotics/ftc-dashboard) is installed,
you can use the FTC dashboard extensions.

## Dashboard properties

Instead of using `@Config`, 
ducklib includes support for a dashboard-[delegated property](https://kotlinlang.org/docs/delegated-properties.html).
The syntax is like this:

```kotlin
val dashProperty by DashboardEx["category/dashProperty", 0]
```

where the first parameter is the name and the second is the initial value for the property.
If no category is specified,
the property will go in the category "Config".

When you change the value in FTC Dashboard ((local ip address)[192.168.43.1:8080/dash])
it will update the value:

```kotlin
package org.firstinspires.ftc.teamcode.opmode

import com.escapevelocity.ducklib.ftc.extensions.DashboardEx
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp
class CustomOpMode : OpMode() {
    val dashConstant by DashboardEx["category/dashConstant", 0.0]
    
    override fun loop() {
        telemetry.addData("dashboard", dashConstant)
    }
}
```
