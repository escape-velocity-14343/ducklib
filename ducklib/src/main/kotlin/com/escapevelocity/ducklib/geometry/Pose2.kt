package com.escapevelocity.ducklib.geometry

data class Pose2(val x: Double, val y: Double, val heading: Double) {
    override fun toString() = "($x, $y ↺ $heading)"
}