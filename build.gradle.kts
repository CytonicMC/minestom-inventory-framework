plugins {
    `java-library`
    `maven-publish`
}

allprojects {
    group = "me.devnatan"
    version = "1.0-SNAPSHOT"

    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    repositories {
        mavenCentral()
    }
}

dependencies {
    implementation(project(":minestomInventoryFramework"))
    implementation(project(":minestomAnvilInput"))
}

subprojects {
    java {
        withSourcesJar()
        withJavadocJar()

        toolchain.languageVersion = JavaLanguageVersion.of(21)
    }

    publishing {
        repositories {
            maven {
                name = "FoxikleCytonicRepository"
                url = uri("https://repo.foxikle.dev/cytonic")
                // Use providers to get the properties or fallback to environment variables
                var u = System.getenv("REPO_USERNAME")
                var p = System.getenv("REPO_PASSWORD")

                if (u == null || u.isEmpty()) {
                    u = "no-value-provided"
                }
                if (p == null || p.isEmpty()) {
                    p = "no-value-provided"
                }

                val user = providers.gradleProperty("FoxikleCytonicRepositoryUsername").orElse(u).get()
                val pass = providers.gradleProperty("FoxikleCytonicRepositoryPassword").orElse(p).get()
                credentials {
                    username = user
                    password = pass
                }
                authentication {
                    create<BasicAuthentication>("basic") {

                    }
                }
            }
        }
        publications {
            create<MavenPublication>("maven") {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
                from(components["java"])
            }
        }
    }
}

