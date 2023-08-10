rootProject.name = "CobblemonTrainers"

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net/")
        gradlePluginPortal()
    }
    includeBuild("build-logic")
}
plugins {
    id("ca.stellardrift.polyglot-version-catalogs") version "5.0.1"
}

listOf(
    "common",
    "fabric",
    "forge"
).forEach { setupProject(it, file(it)) }

fun setupProject(name: String, projectDirectory: File) = setupProject(name) {
    projectDir = projectDirectory
}

inline fun setupProject(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}