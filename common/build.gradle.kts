plugins {
    kotlin("jvm") version "1.6.10"
    java
}

group = "io.github.mslxl.xmusic"
version = "0.0.1-alpha"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.0-native-mt")


    api("com.google.code.gson:gson:2.8.9")
    api("org.hsqldb:hsqldb:2.6.1")
    api("org.apache.httpcomponents:httpclient:4.5.13")

    api("org.slf4j:slf4j-api:2.0.0-alpha5")
    api("ch.qos.logback:logback-core:1.3.0-alpha12")
    api("ch.qos.logback:logback-classic:1.3.0-alpha12")


    api(fileTree("libs"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}