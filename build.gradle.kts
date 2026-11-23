plugins {
    id("fabric-loom") version "1.7-SNAPSHOT"
    id("maven-publish")
}

version = "1.0.0"
group = project.property("maven_group") as String

base {
    archivesName = project.property("archives_base_name") as String
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

minecraft {
}

repositories {
    // Add repositories where dependencies can be found
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("fabric_loader_version")}")

    // Fabric API. This is technically optional, but you probably want it anyway.
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_api_version")}")
}

tasks {
    processResources {
        inputs.property("version", version)
        filteringCharset = "UTF-8"
        filesMatching("fabric.mod.json") {
            expand(
                mapOf(
                    "version" to version,
                    "group" to project.group
                )
            )
        }
    }

    jar {
        from("LICENSE") {
            rename { "${it}_${project.property("archives_base_name")}" }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.property("archives_base_name") as String
            from(components["java"])
        }
    }

    repositories {
        // Add repositories to publish to here.
    }
}
