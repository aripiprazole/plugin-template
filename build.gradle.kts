import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
  kotlin("multiplatform") version "1.7.0-Beta"
  id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
  id("io.gitlab.arturbosch.detekt") version "1.19.0"
}

group = "com.gabrielleeg1"
version = "0.0.1"

apply(plugin = "org.jetbrains.kotlin.multiplatform")
apply(plugin = "org.jlleitschuh.gradle.ktlint")
apply(plugin = "io.gitlab.arturbosch.detekt")

repositories {
  mavenCentral()
  maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
  maven("https://oss.sonatype.org/content/repositories/snapshots")
  maven("https://oss.sonatype.org/content/repositories/central")
}

configure<KtlintExtension> {
  android.set(false)
  additionalEditorconfigFile.set(rootProject.file(".editorconfig"))
}

configure<DetektExtension> {
  buildUponDefaultConfig = true
  allRules = false

  config = files("${rootProject.projectDir}/config/detekt.yml")
  baseline = file("${rootProject.projectDir}/config/baseline.xml")
}

configure<KotlinMultiplatformExtension> {
  jvm {
    withJava()
    compilations.all {
      kotlinOptions.jvmTarget = "1.8"
    }
    testRuns["test"].executionTask.configure {
      testLogging.showStandardStreams = true
      testLogging.exceptionFormat = TestExceptionFormat.FULL
      useJUnitPlatform()
    }
  }

  sourceSets {
    val jvmMain by getting {
      dependencies {
        compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
      }
    }
    val jvmTest by getting
  }
}

val jvmMain = kotlin.jvm().compilations.getByName("main")
val plugins = rootProject.file("server").resolve("plugins")

val jvmJar by tasks.existing(Jar::class) {
  from(jvmMain.runtimeDependencyFiles.map { if (it.isDirectory) it else zipTree(it) })

  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
  destinationDirectory.set(plugins)

  plugins.resolve(archiveFileName.get()).delete()
}
