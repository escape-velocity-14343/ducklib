plugins {
    id("com.android.library") version "8.7.0"
    id("org.jetbrains.kotlin.android") version "2.1.0"
}

group = "org.escapevelocity.ducklib"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
    maven("https://maven.brott.dev")
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly("org.firstinspires.ftc:RobotCore:10.2.0")
    compileOnly("com.acmerobotics.dashboard:dashboard:0.4.16") {
        exclude("org.firstinspires.ftc")
    }
    implementation("com.escapevelocity.ducklib:core")
}

android {
    namespace = "com.escapevelocity.ducklib.ftc"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    publishing {
        multipleVariants {
            withSourcesJar()
            withJavadocJar()
            allVariants()
        }
    }
}