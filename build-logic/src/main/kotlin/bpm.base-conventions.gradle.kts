import com.adarshr.gradle.testlogger.theme.ThemeType
import java.util.*

plugins {
  id("java-library")
  id("net.kyori.indra")
  id("net.kyori.indra.git")
  id("net.kyori.indra.checkstyle")
  id("com.adarshr.test-logger")
//  id("com.gorylenko.gradle-git-properties")
}

version = (version as String)
  .run { if (this.endsWith("-SNAPSHOT")) "$this+${lastCommitHash()}" else this }

indra {
  javaVersions {
    minimumToolchain(17)
    target(8)
  }
  github(Constants.GITHUB_USER, Constants.GITHUB_REPO)
}
//configure<com.gorylenko.GitPropertiesPluginExtension> {
//      failOnNoGitDirectory = false
//      extProperty = "gitProps" // git properties will be put in a map at project.ext.gitProps
//}

tasks.withType<Jar> {
  manifest {
    attributes["Implementation-Title"] = project.name
    attributes["Implementation-Version"] = project.version
    attributes["Build-Jdk"] = "${System.getProperty("java.vendor")} ${System.getProperty("java.vm.version")}"
    attributes["Built-By"] = System.getProperty("user.name")
    attributes["Built-On"] = "${System.getProperty("os.arch")} ${System.getProperty("os.name")} ${System.getProperty("os.version")}"
    attributes["Build-Host"] = System.getenv("HOSTNAME") ?: "null"
    attributes["Build-Date"] = Calendar.getInstance().time.time.toString()
    attributes["Build-Number"] = System.getenv("GITHUB_RUN_NUMBER") ?: "0"
    attributes["Build-Url"] = System.getenv("GITHUB_SERVER_URL")?.let { serverUrl ->
      System.getenv("GITHUB_REPOSITORY")?.let { repo ->
        System.getenv("GITHUB_RUN_NUMBER")?.let { runNumber ->
          "$serverUrl/$repo/actions/runs/$runNumber"
        }
      }
    } ?: "null"
    attributes["Created-By"] = "Gradle ${gradle.gradleVersion}"
    attributes["Build-Workflow"] = System.getenv("GITHUB_WORKFLOW") ?: "null"
    attributes["Build-Actor"] = System.getenv("GITHUB_ACTOR") ?: "null"
    attributes["Build-Runner"] = System.getenv("RUNNER_NAME") ?: "null"
    attributes["Build-OS"] = System.getenv("RUNNER_OS") ?: "null"
    attributes["Build-Repository"] = System.getenv("GITHUB_REPOSITORY") ?: "null"
    attributes["Build-Event"] = System.getenv("GITHUB_EVENT_NAME") ?: "null"
    attributes["Build-Workspace"] = System.getenv("GITHUB_WORKSPACE") ?: "null"
    attributes["Build-Run-Id"] = System.getenv("GITHUB_RUN_ID") ?: "null"
    attributes["Build-Ref"] = System.getenv("GITHUB_REF") ?: "null"
    attributes["Build-Head-Ref"] = System.getenv("GITHUB_HEAD_REF") ?: "null"
    attributes["Build-Base-Ref"] = System.getenv("GITHUB_BASE_REF") ?: "null"
    attributes["Build-Sha"] = System.getenv("GITHUB_SHA") ?: "null"
    attributes["Build-PR"] = System.getenv("GITHUB_PR_NUMBER") ?: "null"
    attributes["Build-Tag"] = System.getenv("GITHUB_TAG") ?: "null"
    attributes["Build-Branch"] = System.getenv("GITHUB_BRANCH") ?: "null"
//    attributes["Build-Revision"] = (project.extra["gitProps"] as Map<String, String>)["git.commit.id"]!!
    attributes["Build-Action"] = System.getenv("GITHUB_ACTION") ?: "null"
    attributes["Build-Is-Codespace"] = System.getenv("CODESPACES") ?: "no"
    attributes["Build-Codespace-Name"] = System.getenv("CODESPACE_NAME") ?: "null"
    attributes["Build-Gitpod-Repository"] = System.getenv("GITPOD_WORKSPACE_CONTEXT_URL") ?: "null"
    attributes["Build-Gitpod-Id"] = System.getenv("GITPOD_WORKSPACE_ID") ?: "null"

    attributes["Build-Gitpod-Url"] = System.getenv("GITPOD_WORKSPACE_URL") ?: "null"
  }
  // To avoid the duplicate handling strategy error
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
  // To add all the dependencies otherwise a "NoClassDefFoundError" error
  from(sourceSets.main.get().output)
}
apply(from="$rootDir/gradle/publish.gradle")

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

//    gitProperties {
//      failOnNoGitDirectory = false
//      extProperty = "gitProps" // git properties will be put in a map at project.ext.gitProps
//    }
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