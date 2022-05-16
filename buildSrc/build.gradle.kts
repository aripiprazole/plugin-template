plugins {
  `kotlin-dsl`
}

repositories {
  maven("https://plugins.gradle.org/m2/")
}

dependencies {
  compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0-Beta")
}
