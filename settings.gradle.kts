plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "ducklib"
include("src:main:test")
findProject(":src:main:test")?.name = "test"
