import com.adarshr.gradle.testlogger.theme.ThemeType

plugins {
  id("java-library")
  id("net.kyori.indra")
  id("net.kyori.indra.git")
  id("net.kyori.indra.checkstyle")
  id("net.kyori.indra.license-header")
  id("com.adarshr.test-logger")
}

version = (version as String)
  .run { if (this.endsWith("-SNAPSHOT")) "$this+${lastCommitHash()}" else this }

indra {
  javaVersions {
    minimumToolchain(17)
    target(17)
  }
  github(Constants.GITHUB_USER, Constants.GITHUB_REPO)
  mitLicense()
}

testlogger {
  theme = ThemeType.MOCHA_PARALLEL
  showPassed = false
}

dependencies {
  testImplementation(libs.jupiterEngine)
}

tasks {
  tasks {
    withType<JavaCompile> {
      options.compilerArgs.add("-Xlint:-processing")
    }
    sequenceOf(javadocJar, javadoc).forEach {
      it.configure {
        onlyIf { false }
      }
    }
  }
}
// Automatically apply Java versioning conventions and have it comply with Semantic Versioning
// Our versioning scheme is: MAJOR.MINOR.PATCH-QUALIFIER
// MAJOR: Major changes to the plugin, such as a complete rewrite
// MINOR: Minor changes to the plugin, such as new features
// PATCH: Bug fixes
// QUALIFIER: A qualifier to the version, such as alpha, beta, or release candidate
// The version is governed from our CI server, and is automatically incremented.
// The CI should never run this.
//String version = rootProject.property("version") as String?
//if (System.getenv("CI") == null && rootProject.property("DO_NOT_CHANGE_VERSION") != "true") {
//    if (version == null || version == "") {
//        version = "SNAPSHOT"
//    } else if (gitBranchName.contains("alpha") || gitBranchName.contains("beta") || gitBranchName.contains("rc")) {
//        version = version + "-SNAPSHOT"
//    } else {
//        version = version
//    }
//}
//println("Version: " + version)
//// The current version based on the most recent tag on the current git branch.
//// Doesn"t mean anything, just a nice to have.
//println("Git Version: " + gitVersion)
//println("Branch: " + gitBranchName)