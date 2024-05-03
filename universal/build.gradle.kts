plugins {
    id("bpm.platform-conventions")
}

tasks.jar {
    archiveClassifier.set("empty")
}

val platforms = setOf(
    projects.brycensPlayerManagerBukkit,
//    projects.brycensPlayerManagerBungee,
//    projects.brycensPlayerManagerVelocity,
//    projects.brycensPlayerManagerFabric,
//    projects.brycensPlayerManagerSponge
).map { it.dependencyProject }

val universal = tasks.register<Jar>("universal") {
    // artifacts.add("archives", this)
    // archiveClassifier.set(null as String?)
    // duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // for (platform in platforms) {
    //     println("Adding platform $platform to universal jar")
    //     println("Platform tasks: ${platform.tasks.names}")
    //     val jarTask = platform.tasks.named<Jar>(":shadowJar")
    //     from(zipTree(jarTask.flatMap { it.archiveFile }))
    // }
}

bpmPlatform {
    jarTask.set(universal)
}