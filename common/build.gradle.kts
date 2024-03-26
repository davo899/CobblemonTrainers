plugins {
    id("cobblemontrainers.base-conventions")
}

architectury {
    common()
}

repositories {
    maven {
        url = uri("https://cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
    maven("https://maven.nucleoid.xyz/")
    maven("https://jitpack.io")
    mavenLocal()
}

dependencies {
    modImplementation(libs.fabricLoader)
    modImplementation("com.google.code.findbugs:jsr305:3.0.2")

    // Economy APIs
    modImplementation("eu.pb4:common-economy-api:1.1.1") // Common Economy API
    modApi("com.github.ExcessiveAmountsOfZombies:OctoEconomyApi:5137175b1c") // OctoEconomyAPI

    modApi("curse.maven:cobblemon-687131:4977486")
    modApi(libs.architectury)

    //shadowCommon group: 'commons-io', name: 'commons-io', version: '2.6'


    compileOnly("net.luckperms:api:${rootProject.property("luckperms_version")}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
