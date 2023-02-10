
rootProject.name = "mrrobotto"

pluginManagement {
    plugins {
        val kotlinPlugVersion = "1.8.10"
        val androidPluginVer = "7.3.1"

        kotlin("multiplatform") version kotlinPlugVersion
        id("com.android.library") version androidPluginVer
        id("org.jetbrains.kotlin.android") version kotlinPlugVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinPlugVersion
        id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
    }

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}