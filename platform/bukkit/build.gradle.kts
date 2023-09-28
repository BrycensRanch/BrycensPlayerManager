plugins {
  id("bpm.shadow-platform")
//  id("net.minecrell.plugin-yml.bukkit")
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
    minecraftVersion("1.19.3")
  }
  shadowJar {
    commonRelocation("org.slf4j")
    commonRelocation("io.leangen.geantyref")
    commonRelocation("net.kyori")
    commonRelocation("org.bstats")
    commonRelocation("io.papermc.lib")
  }
  processResources {
    val replacements = mapOf(
      "modid" to project.name,
      "name" to rootProject.name,
      "author" to "romvnly",
      "version" to project.version.toString(),
      "description" to project.description.toString(),
      "github_url" to Constants.GITHUB_URL
    )
    inputs.properties(replacements)
    filesMatching("plugin.yml") {
      expand(replacements)
    }
  }
}
//bukkit {
//  name = rootProject.name
//  main = "xyz.jpenilla.minimotd.bukkit.BrycensPlayerManagerPlugin"
//  apiVersion = "1.13"
//  website = Constants.GITHUB_URL
//  author = "jmp"
//  softDepend = listOf("ViaVersion")
//  commands {
//    register("minimotd") {
//      description = "BrycensPlayerManager Command"
//      usage = "/minimotd help"
//      permission = "minimotd.admin"
//    }
//  }
//}
