package com.escapevelocity.ducklib.core.math

import com.escapevelocity.ducklib.core.geometry.Vector2

abstract class Spline: (Double) -> Vector2 {
    
}

//fun cubicBezier(start: Vector2, cp1: Vector2, cp2: Vector2, end: Vector2, t: Double) =
//    start * (1 - t).pow(3) + cp1 * 3.0 * (1 - t).pow(2) * t + cp2 * 3.0 * (1 - t) * t.pow(2) + end * t.pow(3)