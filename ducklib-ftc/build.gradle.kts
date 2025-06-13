plugins {
    id("com.android.library") version "8.7.0"
    id("org.jetbrains.kotlin.android") version "2.1.0"
    id("maven-publish")
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
    compileOnly("org.firstinspires.ftc:Hardware:10.2.0")
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

publishing {
    repositories {
        maven {
            name = "dairy"
            url = uri("https://repo.dairy.foundation/#/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        register<MavenPublication>("release") {
            groupId = "com.escapevelocity.ducklib"
            artifactId = "ftc"
            version = "1.0"
            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
