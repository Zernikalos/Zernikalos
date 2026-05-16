/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

import se.bjurr.gitchangelog.plugin.gradle.GitChangelogTask
import org.gradle.internal.os.OperatingSystem
import java.io.File

plugins {
    id("se.bjurr.gitchangelog.git-changelog-gradle-plugin")
}

val zernikalosVersion = project.extra["zernikalosVersion"] as String
val githubOwner = project.extra["githubOwner"] as String
val githubRepo = project.extra["githubRepo"] as String
val repoRoot = project.rootProject.rootDir

tasks.named<GitChangelogTask>("gitChangelog").configure {
    file.set(project.rootProject.file("CHANGELOG.md"))

    val templateFile = project.rootProject.file(".changelog.template")
    val templateContentFromFile = templateFile.readText()
        .replace("__GITHUB_OWNER__", githubOwner)
        .replace("__GITHUB_REPO__", githubRepo)

    templateContent.set(templateContentFromFile)

    fromRepo.set(repoRoot.absolutePath)
    fromRevision.set("")
    toRevision.set("HEAD")
}

tasks.register("generateVersionFile") {
    val outputDir = project.file("src/commonMain/kotlin/zernikalos")
    val outputFile = project.file("$outputDir/ZVersion.kt")

    inputs.property("version", zernikalosVersion)
    outputs.dir(outputDir)

    doLast {
        outputDir.mkdirs()
        val templateFile = project.file(".zversion.kt.template")
        val templateContent = templateFile.readText()
        val processedContent = templateContent.replace("\${project.version}", zernikalosVersion)
        outputFile.writeText(processedContent)
    }
}

tasks.configureEach {
    if (name.startsWith("compile") ||
        name.endsWith("SourcesJar") ||
        name.endsWith("Jar")) {
        dependsOn("generateVersionFile")
        dependsOn(":kotlinUpgradePackageLock")
    }
}

tasks.register("printVersion") {
    description = "Prints the current project version (respects -Pversion parameter)"
    group = "versioning"

    doLast {
        println("=".repeat(60))
        println("📦 Project Version Information")
        println("=".repeat(60))
        val versionFile = project.file("../VERSION.txt")
        println("Version from VERSION.txt: ${if (versionFile.exists()) versionFile.readText().trim() else "(file not found)"}")
        println("Version from -Pversion param: ${project.findProperty("version") ?: "(not provided)"}")
        println("Effective version (used by build): $zernikalosVersion")
        println("=".repeat(60))
    }
}

tasks.register("setVersion") {
    description = "Sets the project version in VERSION.txt. Usage: ./gradlew setVersion -PnewVersion=X.Y.Z"
    group = "versioning"

    doLast {
        val newVersion = project.findProperty("newVersion") as String?
            ?: throw GradleException("Please provide the version with -PnewVersion=X.Y.Z")
        project.file("../VERSION.txt").writeText(newVersion)
        project.version = newVersion
        project.extra.set("zernikalosVersion", newVersion)
        println("VERSION.txt has been updated to: $newVersion")
        println("Now, run './gradlew updateVersion' to apply this version to generated files.")
    }
}

tasks.register("updateVersion") {
    description = "Generates all version-dependent files (constants, podspec, etc.). Run setVersion first."
    group = "versioning"

    finalizedBy("generateVersionFile", "podspec", "jsBrowserDistribution")
}

tasks.named<GitChangelogTask>("gitChangelog").configure {
    description = "Generates changelog from git commits (part of release process)"
    group = "versioning"
    mustRunAfter("updateVersion")
}

tasks.register("releaseCommit") {
    description = "Creates release commit, tags it, regenerates changelog, and amends commit with updated changelog. Format: 'release: 🚀 vX.Y.Z'"
    group = "versioning"
    dependsOn("updateVersion")

    doLast {
        val version = project.extra["zernikalosVersion"] as String

        fun execCommand(command: String) {
            val process = shellProcessBuilder(command, repoRoot)
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().readText()
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                throw GradleException(
                    "Command failed (exit $exitCode): $command\n$output"
                )
            }
        }

        execCommand(
            """
            if [ -f CHANGELOG.md ]; then
                git restore --staged CHANGELOG.md 2>/dev/null || true
                git checkout -- CHANGELOG.md 2>/dev/null || true
            fi && \
            git add . && \
            git commit -m "release: 🚀 v$version"
            """.trimIndent()
        )

        execCommand("git tag -a \"v$version\" -m \"Release v$version\"")

        tasks.named<GitChangelogTask>("gitChangelog").get().actions.forEach { action ->
            action.execute(tasks.getByName("gitChangelog"))
        }

        execCommand(
            """
            git add CHANGELOG.md && \
            git commit --amend --no-edit && \
            git tag -d "v$version" && \
            git tag -a "v$version" -m "Release v$version"
            """.trimIndent()
        )
    }
}

fun shellProcessBuilder(command: String, workingDir: File): ProcessBuilder {
    if (!OperatingSystem.current().isWindows) {
        return ProcessBuilder("sh", "-c", command).directory(workingDir)
    }
    val customShell = System.getenv("ZERNIKALOS_SHELL") // ej: C:\Program Files\Git\bin\sh.exe
    val gitShCandidates = listOfNotNull(
        customShell,
        System.getenv("PROGRAMFILES")?.let { "$it\\Git\\bin\\sh.exe" },
        System.getenv("PROGRAMFILES(X86)")?.let { "$it\\Git\\bin\\sh.exe" },
        System.getenv("LOCALAPPDATA")?.let { "$it\\Programs\\Git\\bin\\sh.exe" },
    ).map(::File).firstOrNull { it.isFile }
    val shell = gitShCandidates
        ?: throw GradleException(
            "releaseCommit needs sh on Windows. Install Git for Windows or set ZERNIKALOS_SHELL to sh.exe"
        )
    return ProcessBuilder(shell.absolutePath, "-c", command).directory(workingDir)
}