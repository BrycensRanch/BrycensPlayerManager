plugins {
  id("bpm.shadow-platform")
  id("net.minecrell.plugin-yml.bukkit")
  id("xyz.jpenilla.run-paper")
}

dependencies {
  implementation(projects.brycensPlayerManagerCommon)
  implementation(libs.slf4jJdk14)
  implementation(libs.adventurePlatformBukkit)
  implementation(libs.bstatsBukkit)
  implementation(libs.paperlib)
  compileOnly(libs.paperApi)
}

tasks {
  runServer {
    minecraftVersion("1.19.4")
  }
  shadowJar {
    commonRelocation("org.slf4j")
    commonRelocation("io.leangen.geantyref")
    commonRelocation("net.kyori")
    commonRelocation("org.bstats")
    commonRelocation("io.papermc.lib")
  }
//  processResources {
//    val replacements = mapOf(
//      "name" to rootProject.name,
//      "author" to "BrycensRanch",
//      "version" to project.version.toString(),
//      "description" to project.description.toString(),
//      "github_url" to Constants.GITHUB_URL
//    )
//    inputs.properties(replacements)
//    filesMatching("plugin.yml") {
//      expand(replacements)
//    }
//  }
}
bukkit {
  name = rootProject.name
  main = Constants.group + ".bukkit.BPMPlugin"
  apiVersion = "1.13"
  website = Constants.GITHUB_URL
  author = Constants.author
  softDepend = listOf("ViaVersion")
  commands {
    register("bpm") {
      description = "BrycensPlayerManager Command"
      usage = "/bpm help"
      permission = "minimotd.admin"
    }
  }
}
