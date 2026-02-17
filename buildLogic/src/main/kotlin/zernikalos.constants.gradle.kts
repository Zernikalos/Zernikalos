/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

val zernikalosGroup = "dev.zernikalos"
val zernikalosName = "zernikalos"
val zernikalosNamedGroup = "$zernikalosGroup.$zernikalosName"
val zernikalosNameCapital = "Zernikalos"
val zernikalosDescription = "Zernikalos Game Engine"

val zernikalosAuthorName = "Aarón Negrín"
val zernikalosLicense = "MPL v2.0"
val zernikalosSiteUrl = "https://zernikalos.dev"

val githubOwner = "Zernikalos"
val githubRepo = "Zernikalos"
val githubPackagesMavenUrl = "https://maven.pkg.github.com/$githubOwner/$githubRepo"
val githubPackagesNpmRegistry = "https://npm.pkg.github.com"

val publishUser = project.findProperty("user") as String? ?: System.getenv("GITHUB_ACTOR") ?: ""
val publishAccessToken = project.findProperty("access_token") as String? ?: System.getenv("GITHUB_TOKEN") ?: ""

val zernikalosVersion: String = run {
    val explicitVersion = project.findProperty("version") as String?
    if (!explicitVersion.isNullOrEmpty() && explicitVersion != "unspecified") {
        explicitVersion
    } else {
        val versionFile = file("../VERSION.txt")
        if (versionFile.exists()) {
            versionFile.readText().trim().takeIf { it.isNotEmpty() }
                ?: throw GradleException("VERSION.txt exists but is empty")
        } else {
            throw GradleException("VERSION.txt not found")
        }
    }
}

project.version = zernikalosVersion

project.extra.apply {
    set("zernikalosGroup", zernikalosGroup)
    set("zernikalosName", zernikalosName)
    set("zernikalosNamedGroup", zernikalosNamedGroup)
    set("zernikalosNameCapital", zernikalosNameCapital)
    set("zernikalosDescription", zernikalosDescription)
    set("zernikalosAuthorName", zernikalosAuthorName)
    set("zernikalosLicense", zernikalosLicense)
    set("zernikalosSiteUrl", zernikalosSiteUrl)
    set("githubOwner", githubOwner)
    set("githubRepo", githubRepo)
    set("githubPackagesMavenUrl", githubPackagesMavenUrl)
    set("githubPackagesNpmRegistry", githubPackagesNpmRegistry)
    set("publishUser", publishUser)
    set("publishAccessToken", publishAccessToken)
    set("zernikalosVersion", zernikalosVersion)
}
