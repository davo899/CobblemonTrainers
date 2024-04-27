plugins {
    id("cobblemontrainers.platform-conventions")
}

architectury {
    platformSetupLoomIde()
    forge()
}

loom {
    forge {
        mixinConfig("mixins.cobblemontrainers-common.json")
    }
}

repositories {
    maven(url = "https://thedarkcolour.github.io/KotlinForForge/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    mavenLocal()
    maven {
        url = uri("https://cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
}

dependencies {
    forge(libs.forge)
    modApi(libs.architecturyForge)

    implementation(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    implementation(libs.kotlinForForge)
    "developmentForge"(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    bundle(project(path = ":common", configuration = "transformProductionForge")) {
        isTransitive = false
    }
    testImplementation(project(":common", configuration = "namedElements"))

    modImplementation("curse.maven:cobblemon-687131:4468321")

    modImplementation(
        "com.selfdot:SelfdotModLibs-forge:${rootProject.property("selfdot_modlibs_version")}"
    )?.let {
        include(it)
    }
}

tasks {
    shadowJar {
        exclude("architectury-common.accessWidener")
        relocate ("com.ibm.icu", "com.cobblemon.mod.relocations.ibm.icu")
    }

    processResources {
        inputs.property("version", rootProject.version)

        filesMatching("META-INF/mods.toml") {
            expand("version" to rootProject.version)
        }
    }
}

//jar {
//    classifier("dev")
//    manifest {
//        attributes(
//                "Specification-Title" to rootProject.mod_id,
//                "Specification-Vendor" to "Cable MC",
//                "Specification-Version" to "1",
//                "Implementation-Title" to rootProject.mod_id,
//                "Implementation-Version" to project.version,
//                "Implementation-Vendor" to "Cable MC",
//        )
//    }
//}
//
//sourcesJar {
//    def commonSources = project(":common").sourcesJar
//    dependsOn commonSources
//    from commonSources.archiveFile.map { zipTree(it) }
//}
//
//components.java {
//    withVariantsFromConfiguration(project.configurations.shadowRuntimeElements) {
//        skip()
//    }
//}