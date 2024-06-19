enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")


dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
            mavenContent { snapshotsOnly() }
        }
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.spongepowered.org/repository/maven-public/")
        maven("https://repo.jpenilla.xyz/snapshots/") {
            mavenContent { snapshotsOnly() }
        }
        mavenLocal()
        maven("https://jitpack.io")
        maven("https://m2.dv8tion.net/releases")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
            mavenContent { snapshotsOnly() }
        }
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.jeff-media.com/nexus/repository/jeff-media-public/")
        maven("https://libraries.minecraft.net/")
        maven("https://repo.dmulloy2.net/repository/public/")
        maven("https://repo.spongepowered.org/maven/")
        maven("https://nexus.scarsz.me/content/groups/public/")
        maven("https://repo.glaremasters.me/repository/towny/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.architectury.dev/")
        maven("https://maven.quiltmc.org/repository/release/")
        maven("https://repo.jpenilla.xyz/snapshots/")
    }
    includeBuild("build-logic")

}

plugins {
  id("quiet-fabric-loom") version "1.7.287"
}

rootProject.name = "BrycensPlayerManager"


sequenceOf(
        "common",
        "universal"
).forEach { setupSubproject(it) }


// Now we can include the platform projects


inline fun setupSubproject(name: String) {
    include("${rootProject.name}-$name")
    project(":${rootProject.name}-$name").projectDir = file(name)
}
sequenceOf(
        "bukkit",
//        "fabric",
//        "bungee",
//        "velocity",
//        "sponge"
).forEach {
    include("${rootProject.name}-$it")
    project(":${rootProject.name}-$it").projectDir = file("platform/$it")
}