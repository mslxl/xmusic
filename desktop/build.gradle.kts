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

    implementation("com.github.mslxl:KtSwing:2.1.2")

    implementation("uk.co.caprica:vlcj:4.7.1")

    implementation(fileTree("libs"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}