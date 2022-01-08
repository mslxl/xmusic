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
    implementation(kotlin("stdlib"))
    implementation(project(":common"))
    implementation("com.github.mslxl:KtSwing:2.1.0")
    implementation(fileTree("libs"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}