pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "ducklib-ftc"


includeBuild("../ducklib") {
    dependencySubstitution {
        substitute(module("com.escapevelocity.ducklib:core")).using(project(":"))
    }
}