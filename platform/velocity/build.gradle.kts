plugins {
  id("bpm.shadow-platform")
  id("net.kyori.blossom")
  id("xyz.jpenilla.run-velocity")
}

dependencies {
  implementation(projects.brycensPlayerManagerCommon)
  implementation(libs.libbyVelocity)
//  kapt(libs.velocityApi)
  implementation(libs.bstatsVelocity)
  compileOnly(libs.cloudVelocity)
  compileOnly(libs.velocityApi)
  annotationProcessor(libs.velocityApi)
}

val platform = project.name.substringAfter("-")
tasks {
  shadowJar {
    configureForNativeAdventurePlatform()
    platformRelocation(platform, "io.leangen.geantyref")
    platformRelocation(platform, "org.bstats")
    platformRelocation(platform, "net.byteflux.libby")
    platformRelocation(platform, "cloud.commandframework")

//    commonRelocation("xyz.jpenilla.minimotd.common")
//    commonRelocation("net.byteflux.libby")
  }
  runVelocity {
    velocityVersion(libs.versions.velocityApi.get())
  }
}

blossom {
  val file = "src/main/java/me/brycensranch/BrycensPlayerManager/velocity/BPMPlugin.java"
  mapOf(
    "project.name" to project.name,
    "description" to description as String,
    "url" to Constants.GITHUB_URL,
    "author" to Constants.author,
  ).forEach { (k, v) ->
    replaceToken("\${$k}", v, file)
  }
}
