plugins {
  id("com.github.johnrengelman.shadow") version "8.1.0"
//    id "com.github.sherter.google-java-format" version "0.9"
//    id "com.github.spotbugs" version "5.0.13"
  id("com.gorylenko.gradle-git-properties") version "2.4.1"
//    id "com.palantir.git-version" version "1.0.0"
  id("com.moonlitdoor.git-version") version "0.1.1"

  id("name.remal.common-ci") version "1.5.0"
//  id("com.github.spotbugs") version "5.0.13"
//  id("com.diffplug.spotless") version "6.16.0"
//
//
//
//  id("org.gradle.crypto.checksum") version "1.4.0"
  java
  jacoco
  idea
  checkstyle
  signing
  `kotlin-dsl`
}
repositories {
  gradlePluginPortal()
}

dependencies {
  implementation(libs.indraCommon)
  implementation(libs.shadow)
    implementation(libs.gitProperties)
  implementation(libs.testLogger)

  // https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}


//apply(from="$rootDir/plugin.gradle")