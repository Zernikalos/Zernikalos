
rootProject.name = "zernikalos"

pluginManagement {
    plugins {
        val kotlinPlugVersion = "1.8.21"
        val androidPluginVer = "7.4.0"

        kotlin("multiplatform") version kotlinPlugVersion
        id("com.android.library") version androidPluginVer
        id("org.jetbrains.kotlin.android") version kotlinPlugVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinPlugVersion
        id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
        // id("org.lwjgl.plugin") version "0.0.30"
    }

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}