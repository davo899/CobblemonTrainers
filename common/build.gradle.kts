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
    mavenLocal()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    modImplementation(libs.fabricLoader)
    modImplementation("com.google.code.findbugs:jsr305:3.0.2")
    modApi("curse.maven:cobblemon-687131:4977486")
    modApi(libs.architectury)

    compileOnly("net.luckperms:api:${rootProject.property("luckperms_version")}")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
