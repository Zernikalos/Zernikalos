/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import java.time.Year

// Constants
val zernikalosGroup = "dev.zernikalos"
val zernikalosName = "zernikalos"
val zernikalosNamedGroup = "$zernikalosGroup.$zernikalosName"
val zernikalosNameCapital = "Zernikalos"
val zernikalosDescription = "Zernikalos Game Engine"

val zernikalosAuthorName = "Aarón Negrín"
val zernikalosLicense = "MPL v2.0"
val zernikalosSiteUrl = "https://zernikalos.dev"

// GitHub Packages configuration
val githubOwner = "Zernikalos"
val githubRepo = "Zernikalos"
val githubPackagesMavenUrl = "https://maven.pkg.github.com/$githubOwner/$githubRepo"
val githubPackagesNpmRegistry = "https://npm.pkg.github.com"

val publishUser = project.findProperty("user") as String? ?: System.getenv("GITHUB_ACTOR") ?: ""
val publishAccessToken = project.findProperty("access_token") as String? ?: System.getenv("GITHUB_TOKEN") ?: ""

var zernikalosVersion: String
    get() {
        // Check if version was explicitly set via -Pversion parameter first
        val explicitVersion = project.findProperty("version") as String?
        if (!explicitVersion.isNullOrEmpty() && explicitVersion != "unspecified") {
            return explicitVersion
        }
        // Otherwise, read from file (more reliable for plugins like CocoaPods)
        val versionFile = file("VERSION.txt")
        return if (versionFile.exists()) {
            versionFile.readText().trim().takeIf { it.isNotEmpty() }
                ?: throw GradleException("VERSION.txt exists but is empty")
        } else {
            throw GradleException("VERSION.txt not found")
        }
    }
    set(value) {
        file("VERSION.txt").writeText(value)
        // Sync with project version
        project.version = value
    }

project.version = zernikalosVersion

plugins {
    kotlin("multiplatform") version libs.versions.kotlin.get() apply true
    kotlin("native.cocoapods") version libs.versions.kotlin.get()
    id("com.android.library") version libs.versions.androidGradlePlugin.get() apply true
    id("org.jetbrains.kotlin.android") version libs.versions.kotlin.get() apply false
    id("org.jetbrains.kotlin.plugin.serialization") version libs.versions.kotlin.get()
    id("maven-publish")
    id("org.jetbrains.dokka") version libs.versions.dokka.get()
    id("com.github.ben-manes.versions") version libs.versions.versionsPlugin.get()
    id("se.bjurr.gitchangelog.git-changelog-gradle-plugin") version libs.versions.gitChangelogPlugin.get()
}

allprojects {
    group = zernikalosGroup
    version = zernikalosVersion
}

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    google()
}

publishing {
    repositories {
        maven {
            url = uri(githubPackagesMavenUrl)
            credentials {
                username = publishUser
                password = publishAccessToken
            }
        }
    }
}

android {
    namespace=zernikalosGroup
    compileSdk=36

    defaultConfig {
        minSdk=24

        version=zernikalosVersion

        testInstrumentationRunner="androidx.test.runner.AndroidJUnitRunner"
        // consumerProguardFiles="consumer-rules.pro"
    }
    lint {
        abortOnError=false
    }

}

kotlin {
    applyDefaultHierarchyTemplate()

    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    androidTarget {
        publishLibraryVariants("release", "debug")
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    //jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
                }
            }
        }
    }

    js(IR) {
        outputModuleName.set("@zernikalos/zernikalos")
        version = zernikalosVersion

        compilations["main"].packageJson {
            customField("author", zernikalosAuthorName)
            customField("description", zernikalosDescription)
            customField("license", zernikalosLicense)
            customField("version", zernikalosVersion)
            customField("repository", mapOf(
                "type" to "git",
                "url" to "https://github.com/$githubOwner/$githubRepo"
            ))
            customField("publishConfig", mapOf(
                "registry" to githubPackagesNpmRegistry
            ))
            customField("types", "kotlin/@zernikalos/zernikalos.d.ts")
            @OptIn(ExperimentalKotlinGradlePluginApi::class)
            compilerOptions.freeCompilerArgs.add("-Xir-minimized-member-names=false")
            compilerOptions.freeCompilerArgs.add("-Xes-long-as-bigint")
        }

        browser {
            binaries.executable()
            commonWebpackConfig {
                output?.libraryTarget = "umd"
                output?.library = zernikalosName
                mode = org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.DEVELOPMENT
                sourceMaps = true
            }
            generateTypeScriptDefinitions()
            testTask {
                useMocha()
            }
        }
    }

    val xcf = XCFramework(zernikalosNameCapital)
    val appleTargets = listOf(
        macosArm64(),
        iosArm64(),
        iosSimulatorArm64()
    )

    appleTargets.forEach {
        it.binaries.framework {
            isStatic = true
            baseName = zernikalosNameCapital
            binaryOption("bundleId", zernikalosNamedGroup)
            binaryOption("bundleVersion", zernikalosVersion)
            debuggable = buildType.name != "RELEASE"
            xcf.add(this)
        }
        //it.compilerOptions.freeCompilerArgs.add("-Xbinary=preCodegenInlineThreshold=40")
    }

    cocoapods {
        name = zernikalosNameCapital
        summary = zernikalosDescription
        license = zernikalosLicense
        authors = zernikalosAuthorName
        homepage = zernikalosSiteUrl
        version = zernikalosVersion
        framework {
            baseName = zernikalosNameCapital
        }
    }

    sourceSets {
        all {
            //languageSettings.optIn("zernikalos.OptInAnnotation")
            languageSettings.optIn("kotlin.js.ExperimentalJsExport")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
            languageSettings.optIn("kotlin.uuid.ExperimentalUuidApi")
        }

        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.serialization.protobuf)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val oglMain by creating {
            kotlin.srcDir("src/oglMain/kotlin")
            dependsOn(commonMain.get())
        }

        val metalMain by creating {
            kotlin.srcDir("src/metalMain/kotlin")
            dependsOn(commonMain.get())
        }

        val webgpuMain by creating {
            kotlin.srcDir("src/webgpuMain/kotlin")
            dependsOn(commonMain.get())
        }

        androidMain {
            kotlin.srcDir("src/androidMain/kotlin")
            dependsOn(oglMain)
        }

        androidUnitTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        jsMain {
            // Required for compatibility with zdebugger (see webpack file)
            kotlin.srcDir("src/jsMain/kotlin")
            dependencies {
                implementation(npm("ua-parser-js","1.0.37"))
                implementation(devNpm("string-replace-loader", "3.2.0"))
            }
            dependsOn(webgpuMain)
        }

        jsTest {
            // dependsOn(commonTest) - Redundant, already included in Kotlin Target Hierarchy template
        }

        val macosArm64Main by getting {
            dependsOn(metalMain)
            kotlin.srcDir("src/macosMain/kotlin")
        }

        iosMain {
            dependsOn(metalMain)
            kotlin.srcDir("src/iosMain/kotlin")
        }

    }
}

fun getYear(): String {
    return Year.now().toString()
}

// ============================================================================
// DOCKKA CONFIGURATION
// ============================================================================

dokka {
    moduleName.set(zernikalosNameCapital)
    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory.set(projectDir.resolve("src"))
            remoteUrl("https://github.com/$githubOwner/$githubRepo/tree/main/src")
            remoteLineSuffix.set("#L")
        }
    }
    pluginsConfiguration.html {
        //templatesDir = file("docs/dokkaTemplates")
        customStyleSheets.from("docsAssets/zk-docs-styles.css")
        customAssets.from("docsAssets/logo-icon.svg")
        footerMessage.set("© ${getYear()} $zernikalosNameCapital")
    }
}

// ============================================================================
// GIT CHANGELOG CONFIGURATION
// ============================================================================

// Configure the default gitChangelog task
tasks.named("gitChangelog", se.bjurr.gitchangelog.plugin.gradle.GitChangelogTask::class.java).configure {
    file.set(file("CHANGELOG.md"))

    // Read template from file and replace placeholders with actual values
    val templateFile = file(".changelog.template")
    val templateContentFromFile = templateFile.readText()
        .replace("__GITHUB_OWNER__", githubOwner)
        .replace("__GITHUB_REPO__", githubRepo)

    templateContent.set(templateContentFromFile)

    fromRepo.set(file(".").absolutePath)

    // Process all tags from the beginning to HEAD
    // Empty string means from the beginning of the repository
    // This allows the plugin to process all release tags
    fromRevision.set("")
    toRevision.set("HEAD")
}

// Custom Tasks

// ============================================================================
// VERSION MANAGEMENT TASKS
// ============================================================================

// Generates ZVersion.kt file with current version number.
// This task runs automatically before compilation tasks.
tasks.register("generateVersionFile") {
    val outputDir = file("src/commonMain/kotlin/zernikalos")
    val outputFile = file("$outputDir/ZVersion.kt")

    inputs.property("version", zernikalosVersion)
    outputs.dir(outputDir)

    doLast {
        outputDir.mkdirs()
        val templateFile = file(".zversion.kt.template")
        val templateContent = templateFile.readText()
        val processedContent = templateContent.replace("\${project.version}", zernikalosVersion)
        outputFile.writeText(processedContent)
    }
}

// Make sure that the version constants are generated before any compilation task.
tasks.configureEach {
    if (name.startsWith("compile") ||
        name.endsWith("SourcesJar") ||
        name.endsWith("Jar")) {
        dependsOn("generateVersionFile")
        dependsOn("kotlinUpgradePackageLock")
    }
}

// Prints current project version information (useful for debugging and CI).
// Respects -Pversion parameter override.
tasks.register("printVersion") {
    description = "Prints the current project version (respects -Pversion parameter)"
    group = "versioning"

    doLast {
        println("=".repeat(60))
        println("📦 Project Version Information")
        println("=".repeat(60))
        println("Version from VERSION.txt: ${file("VERSION.txt").readText().trim()}")
        println("Version from -Pversion param: ${project.findProperty("version") ?: "(not provided)"}")
        println("Effective version (used by build): $zernikalosVersion")
        println("=".repeat(60))
    }
}

// Sets the project version in VERSION.txt.
// Usage: ./gradlew setVersion -PnewVersion=X.Y.Z
tasks.register("setVersion") {
    description = "Sets the project version in VERSION.txt. Usage: ./gradlew setVersion -PnewVersion=X.Y.Z"
    group = "versioning"

    doLast {
        val newVersion = project.findProperty("newVersion") as String?
            ?: throw GradleException("Please provide the version with -PnewVersion=X.Y.Z")
        zernikalosVersion = newVersion
        println("VERSION.txt has been updated to: $zernikalosVersion")
        println("Now, run './gradlew updateVersion' to apply this version to generated files.")
    }
}

// Aggregator task that generates all version-dependent files.
// Run this after setVersion to propagate version changes.
tasks.register("updateVersion") {
    description = "Generates all version-dependent files (constants, podspec, etc.). Run setVersion first."
    group = "versioning"

    // This task acts as an aggregator. It will use the version currently in VERSION.txt.
    finalizedBy("generateVersionFile", "podspec", "jsBrowserDistribution")
}

// Configure gitChangelog task to run as part of release process
tasks.named("gitChangelog", se.bjurr.gitchangelog.plugin.gradle.GitChangelogTask::class.java).configure {
    description = "Generates changelog from git commits (part of release process)"
    group = "versioning"
    // This will run after updateVersion to ensure version is set correctly
    mustRunAfter("updateVersion")
}

// Creates release commit and tag, regenerates changelog, and finalizes everything in one atomic operation
tasks.register("releaseCommit") {
    description = "Creates release commit, tags it, regenerates changelog, and amends commit with updated changelog. Format: 'release: 🚀 vX.Y.Z'"
    group = "versioning"
    dependsOn("updateVersion")

    doLast {
        fun execCommand(command: String): Int {
            val process = ProcessBuilder("sh", "-c", command)
                .directory(rootDir)
                .redirectErrorStream(true)
                .start()
            process.waitFor()
            return process.exitValue()
        }

        // Step 1: Create commit excluding CHANGELOG.md
        execCommand(
            """
            if [ -f CHANGELOG.md ]; then
                git restore --staged CHANGELOG.md 2>/dev/null || true
                git checkout -- CHANGELOG.md 2>/dev/null || true
            fi && \
            git add . && \
            git commit -m "release: 🚀 v$zernikalosVersion"
            """.trimIndent()
        )

        // Step 2: Create tag (needed for changelog generation)
        execCommand("git tag -a \"v$zernikalosVersion\" -m \"Release v$zernikalosVersion\"")

        // Step 3: Regenerate changelog with new version (execute task directly)
        tasks.named<se.bjurr.gitchangelog.plugin.gradle.GitChangelogTask>("gitChangelog").get().actions.forEach { action ->
            action.execute(tasks.getByName("gitChangelog"))
        }

        // Step 4: Amend commit with changelog and update tag
        execCommand(
            """
            git add CHANGELOG.md && \
            git commit --amend --no-edit && \
            git tag -d "v$zernikalosVersion" && \
            git tag -a "v$zernikalosVersion" -m "Release v$zernikalosVersion"
            """.trimIndent()
        )
    }
}

// ============================================================================
// PUBLISHING CONFIGURATION TASKS
// ============================================================================

// Generates .npmrc file with GitHub Packages authentication.
// Used for publishing npm packages to GitHub Packages registry.
tasks.register<Copy>("generateNpmrc") {
    from(".npmrc.template")
    into(layout.buildDirectory.dir("js").get().toString())
    rename(".npmrc.template", ".npmrc")
    filter { line ->
        line.replace("\${GITHUB_USER}", publishUser)
            .replace("\${GITHUB_TOKEN}", publishAccessToken)
    }
}

// ============================================================================
// TEST CONFIGURATION
// ============================================================================

tasks.withType<org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest>().configureEach {
    testLogging {
        events("passed", "skipped", "failed")
    }
}
