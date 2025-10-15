plugins {
    `java-library`
    `maven-publish`
    id("java")
}

group = "me.devnatan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.minestom)
    api(project(":minestomInventoryFramework"))
}