plugins {
    id("bpm.platform-conventions")
}

tasks.jar {
    archiveClassifier.set("empty")
}

val platforms = setOf(
    projects.bedrockPlayerManagerBukkit,
//    projects.bedrockPlayerManagerBungee,
//    projects.bedrockPlayerManagerVelocity,
//    projects.bedrockPlayerManagerFabric,
//    projects.bedrockPlayerManagerSponge
).map { it.dependencyProject }

val universal = tasks.register<Jar>("universal") {
    artifacts.add("archives", this)
    archiveClassifier.set(null as String?)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    for (platform in platforms) {
        val jarTask = platform.bpmPlatform.jarTask
        from(zipTree(jarTask.flatMap { it.archiveFile }))
    }
}

bpmPlatform {
    jarTask.set(universal)
}