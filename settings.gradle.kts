
rootProject.name = "zernikalos"

pluginManagement {
    plugins {
        val kotlinPluginVersion = "2.0.0-RC3"
        val androidPluginVer = "8.1.0"

        kotlin("multiplatform") version kotlinPluginVersion
        id("com.android.library") version androidPluginVer
        id("org.jetbrains.kotlin.android") version kotlinPluginVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinPluginVersion
        id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
        // id("org.lwjgl.plugin") version "0.0.30"
    }

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}