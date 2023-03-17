import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency
import java.util.Locale

plugins {
  id("bpm.shadow-platform")
  id("org.spongepowered.gradle.plugin")
}

dependencies {
  implementation(projects.brycensPlayerManagerCommon)
}

sponge {
  injectRepositories(false)
  apiVersion("8.1.0-SNAPSHOT")
  plugin(rootProject.name.toLowerCase(Locale.ENGLISH)) {
    loader {
      name(PluginLoaders.JAVA_PLAIN)
      version("1.0")
    }
    license("MIT")
    displayName(rootProject.name)
    entrypoint(Constants.group + ".sponge8.BPMPlugin")
    description(project.description)
    links {
      homepage(Constants.GITHUB_URL)
      source(Constants.GITHUB_URL)
      issues("${Constants.GITHUB_URL}/issues")
    }
    contributor(Constants.author) {
      description("Lead Developer")
    }
    dependency("spongeapi") {
      loadOrder(PluginDependency.LoadOrder.AFTER)
      optional(false)
    }
  }
}
val platform = project.name.substringAfter("-")

tasks {
  shadowJar {
    configureForNativeAdventurePlatform()
    platformRelocation(platform, "xyz.jpenilla.minimotd.common")
    dependencies {
      exclude(dependency("io.leangen.geantyref:geantyref"))
    }
  }
}
