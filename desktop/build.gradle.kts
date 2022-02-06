import org.gradle.jvm.tasks.Jar

plugins {
    kotlin("jvm") version "1.6.10"
    java
    application
}

group = "io.github.mslxl.xmusic"
version = "0.0.1-alpha"

application {
    mainClass.set("io.github.mslxl.xmusic.desktop.App")
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation(project(":common"))
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.0-native-mt")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.0-native-mt")
    implementation("org.xerial:sqlite-jdbc:3.36.0.3")

    implementation("com.github.mslxl:KtSwing:2.1.7")

    implementation("uk.co.caprica:vlcj:4.7.1")

    implementation("com.formdev:flatlaf:2.0.1")

    implementation("ch.qos.logback:logback-core:1.3.0-alpha12")
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha12")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

val fatJar = task("fatJar", type = Jar::class) {
    baseName = "${project.name}-fat"
    manifest {
        attributes["Implementation-Title"] = "XMusic Flatjar"
        attributes["Implementation-Version"] = version
        attributes["Main-Class"] = application.mainClass
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    exclude(
        "META-INF/*.RSA",
        "META-INF/*.SF",
        "META-INF/*.DSA",
        "META-INF/INDEX.LIST",
        "META-INF/versions/*",
        "module-info.class",
        "LICENSE.txt"
    )
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    with(tasks.jar.get() as CopySpec)

}

tasks {
    "build" {
        dependsOn(fatJar)
    }
}