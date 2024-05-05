import java.util.*

plugins {
  id("net.kyori.blossom")
  id("bpm.base-conventions")
}

tasks.jar {
  from(rootProject.file("LICENSE")) {
    rename { "license_${rootProject.name.lowercase(Locale.getDefault())}.txt" }
  }
}

dependencies {
  api(libs.configurateHocon)
  api(platform(libs.adventureBom))
  api(libs.adventureApi)
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

sourceSets.main {
  blossom {
    javaSources {
      property("PLUGIN_NAME", rootProject.name)
      property("PLUGIN_VERSION", project.version.toString())
      property("PLUGIN_WEBSITE", Constants.GITHUB_URL)
      property("GITHUB_USER", Constants.GITHUB_USER)
      property("GITHUB_REPO", Constants.GITHUB_REPO)
    }
  }
}