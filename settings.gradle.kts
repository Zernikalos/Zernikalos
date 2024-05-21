/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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