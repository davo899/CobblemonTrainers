import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    id("dev.architectury.loom")
    id("architectury-plugin")
    kotlin("jvm")
}

repositories {
    mavenCentral()
    maven("https://maven.impactdev.net/repository/development/")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

architectury {
    minecraft = project.property("mc_version").toString()
}

loom {
    silentMojangMappingsLicense()

    @Suppress("UnstableApiUsage")
    mixin {
        defaultRefmapName.set("cobblemontrainers-${project.name}-refmap.json")
    }
}

dependencies {
    minecraft("net.minecraft:minecraft:${rootProject.property("mc_version")}")
    mappings("net.fabricmc:yarn:${rootProject.property("yarn_version")}")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    withType<Jar> {
        from(rootProject.file("LICENSE"))
    }
}