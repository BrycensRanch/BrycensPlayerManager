
plugins {
    id("bpm.platform-conventions")
}


val platforms = setOf(
    projects.brycensPlayerManagerBungee,
    projects.brycensPlayerManagerSponge,
//    projects.brycensPlayerManagerVelocity,
    projects.brycensPlayerManagerFabric,
    projects.brycensPlayerManagerBukkit
).map { it.dependencyProject }

tasks {
    jar {
        archiveClassifier.set("empty")
    }
    build {
        dependsOn(universal)
    }
    sourcesJar {
        subprojects.forEach { subproject ->
            if (subproject == project) return@forEach
            val platformSourcesJarTask = subproject.tasks.findByName("sourcesJar") as? Jar ?: return@forEach
            dependsOn(platformSourcesJarTask)
            from(zipTree(platformSourcesJarTask.archiveFile))
        }
    }
}


val universal = tasks.register<Jar>("universal") {
    artifacts.add("archives", this)
    archiveClassifier.set(null as String?)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveFileName.set("${rootProject.name}-${project.version}.jar")


    for (platform in platforms) {
        println("Adding platform: ${platform.name} to universal jar")
        val jarTask = platform.bpmPlatform.jarTask
        from(zipTree(jarTask.flatMap { it.archiveFile }))
    }
}
bpmPlatform {
    jarTask.set(universal)
}