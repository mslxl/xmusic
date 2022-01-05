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

    implementation("com.google.code.gson:gson:2.8.9")
    implementation("org.hsqldb:hsqldb:2.6.1")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation(fileTree("libs"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}