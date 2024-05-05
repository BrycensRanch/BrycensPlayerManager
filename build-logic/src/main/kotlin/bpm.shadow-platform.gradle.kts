plugins {
  id("bpm.platform-conventions")
  id("io.github.goooler.shadow")
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
