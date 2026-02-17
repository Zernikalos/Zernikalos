/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

import org.jetbrains.dokka.gradle.engine.parameters.KotlinPlatform
import java.time.Year

plugins {
    id("org.jetbrains.dokka")
}

val zernikalosNameCapital = project.extra["zernikalosNameCapital"] as String
val githubOwner = project.extra["githubOwner"] as String
val githubRepo = project.extra["githubRepo"] as String

dokka {
    moduleName.set(zernikalosNameCapital)
    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory.set(project.projectDir.resolve("src"))
            remoteUrl("https://github.com/$githubOwner/$githubRepo/tree/main/src")
            remoteLineSuffix.set("#L")
        }
    }

    val jsLike = listOf("jsMain" to "JS", "webgpuMain" to "WebGPU")
    val androidLike = listOf("androidMain" to "Android", "oglMain" to "OGL")
    val appleLike = listOf("metalMain" to "Metal", "macosArm64Main" to "macOS", "iosMain" to "iOS")

    jsLike.forEach { (name, label) ->
        dokkaSourceSets.named(name) {
            analysisPlatform.set(KotlinPlatform.JS)
            displayName.set(label)
        }
    }

    androidLike.forEach { (name, label) ->
        dokkaSourceSets.named(name) {
            analysisPlatform.set(KotlinPlatform.AndroidJVM)
            displayName.set(label)
        }
    }

    appleLike.forEach { (name, label) ->
        dokkaSourceSets.named(name) {
            analysisPlatform.set(KotlinPlatform.Native)
            displayName.set(label)
        }
    }

    pluginsConfiguration.html {
        templatesDir = project.file("../docs/dokkaTemplates")
        customStyleSheets.from(project.file("../docsAssets/zk-docs-styles.css"))
        customAssets.from(project.file("../docsAssets/logo-icon.svg"))
        footerMessage.set("© ${Year.now()} $zernikalosNameCapital")
    }
}
