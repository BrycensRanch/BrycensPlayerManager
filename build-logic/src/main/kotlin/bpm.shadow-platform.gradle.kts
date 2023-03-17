plugins {
  id("bpm.platform-conventions")
  id("com.github.johnrengelman.shadow")
}

tasks {
  jar {
    archiveClassifier.set("unshaded")
  }
  shadowJar {
    archiveClassifier.set(null as String?)
    commonConfiguration()
  }
}

extensions.configure<BPMPlatformExtension> {
  jarTask.set(tasks.shadowJar)
}
