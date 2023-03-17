plugins {
  id("net.kyori.blossom")
  id("bpm.base-conventions")
}

tasks.jar {
  from(rootProject.file("LICENSE")) {
    rename { "license_${rootProject.name.toLowerCase()}.txt" }
  }
  from(rootProject.file("gradle/libs.versions.yml")) {
    rename { "libs.versions.yml" }
  }
}

dependencies {
  api(libs.configurateHocon)
  api(platform(libs.adventureBom))
  api(libs.adventureApi)
  api(libs.cloud)
  api(libs.cloudAnnotations)
    implementation("org.projectlombok:lombok:1.18.22")
    annotationProcessor(libs.cloudAnnotations)
  api(libs.cloudBrigadier)
  api(libs.cloudMinecraftExtras) {
    isTransitive = false // we depend on adventure separately
  }
  api(libs.adventureTextSerializerPlain)
  api(libs.adventureTextSerializerGson) {
    exclude("com.google.code.gson", "gson")
  }
  api(libs.minimessage)
  compileOnlyApi(libs.slf4jApi)
  compileOnlyApi(libs.checkerQual)
  compileOnlyApi(libs.gson)
  compileOnlyApi(libs.guava)
}

blossom {
  val file = "src/main/java/me/brycensranch/BrycensPlayerManager/common/Constants.java"
  mapOf(
    "PLUGIN_NAME" to rootProject.name,
    "PLUGIN_VERSION" to project.version.toString(),
    "PLUGIN_WEBSITE" to Constants.GITHUB_URL,
    "GITHUB_USER" to Constants.GITHUB_USER,
    "GITHUB_REPO" to Constants.GITHUB_REPO
  ).forEach { (k, v) ->
    replaceToken("\${$k}", v, file)
  }
}
