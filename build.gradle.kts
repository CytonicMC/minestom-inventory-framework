plugins {
    `java-library`
    `maven-publish`
    id("java")
    id("com.gradleup.shadow") version "9.0.2"
}

group = "net.cytonic"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.foxikle.dev/cytonic")
    mavenLocal()
}

dependencies {
    compileOnly("net.minestom:minestom:2025.08.18-1.21.8")
    api("me.devnatan:inventory-framework-platform:1.0-CYTONIC")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
