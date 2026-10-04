plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "3.1.0"
    id("com.gradleup.shadow") version "9.6.1"
}

group = "net.chamosmp.pvpcore"
version = "1.0.0"

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "chamosmpRepoReleases"
        url = uri("https://maven.chamosmp.net/releases")
    }
    maven {
        name = "Eldonexus"
        url = uri("https://eldonexus.de/repository/maven-public/")
    }
    maven {
        name = "PlaceholderAPI"
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")
    implementation("net.chamosmp.sqdlib:sqdlib-paper:3.+")

    compileOnly("net.strokkur.commands:annotations-paper:2.3.0")
    annotationProcessor("net.strokkur.commands:processor-paper:2.3.0")

    compileOnly("me.clip:placeholderapi:2.12.3")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.4.5")
}

tasks {
    runServer {
        downloadPlugins {
            modrinth("lKEzGugV", "2.12.3")
            modrinth("z4HZZnLr", "2.15.4")
        }

        minecraftVersion("26.2")
    }
    runPaper.folia.registerTask()

    shadowJar {
        configurations = project.configurations.runtimeClasspath.map { setOf(it) }

        relocate("net.chamosmp.sqdlib", "net.chamosmp.pvpcore.libs.sqdlib")
    }

    processResources {
        val props = mapOf("version" to project.version)
        inputs.properties(props)
        filteringCharset = "UTF-8"

        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-Xlint:deprecation")
    }
}

java.toolchain.languageVersion = JavaLanguageVersion.of(25)