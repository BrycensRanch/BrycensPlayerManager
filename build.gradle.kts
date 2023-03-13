plugins {
    id("bpm.build-logic")
    id("bpm.platform-conventions")
}
tasks.jar {
    archiveClassifier.set("empty")
}

val platforms = setOf(
    projects.bedrockPlayerManagerBukkit,
//    projects.bedrockPlayerManagerBungee,
//    projects.bedrockPlayerManagerVelocity,
//    projects.bedrockPlayerManagerSponge,
//    projects.bedrockPlayerManagerFabric,
).map { it.dependencyProject }

val universal = tasks.register<Jar>("universal") {
    artifacts.add("archives", this)
    archiveClassifier.set(null as String?)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    for (platform in platforms) {
        val jarTask = platform.miniMOTDPlatform.jarTask
        from(zipTree(jarTask.flatMap { it.archiveFile }))
    }
}

miniMOTDPlatform {
    jarTask.set(universal)
}