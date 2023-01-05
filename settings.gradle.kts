
rootProject.name = "mrrobotto"

pluginManagement {
    plugins {
        val kotlinVersion = "1.8.0"

        kotlin("multiplatform") version kotlinVersion
        id("com.android.library") version "7.3.1"
        id("org.jetbrains.kotlin.android") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
    }

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}