plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    maven("https://maven.architectury.dev/")
    maven("https://maven.fabricmc.net/")
    maven("https://maven.minecraftforge.net/")
}

dependencies {
    implementation(libs.kotlin)
    implementation(libs.shadow)
    implementation(libs.loom)
    implementation(libs.architecturyPlugin)
}