// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.ktfmt) apply false
    alias(libs.plugins.ksp) apply false
    id("org.sonarqube") version "4.4.1.3373"
}
sonar {
  properties {
    property("sonar.projectKey", "PlateSwipe_PlateSwipe")
    property("sonar.organization", "plateswipe")
    property("sonar.host.url", "https://sonarcloud.io")
  }
}