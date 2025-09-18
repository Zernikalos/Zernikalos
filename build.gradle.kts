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
var zernikalosVersion: String
    get() = file("VERSION.txt").readText().trim()
    set(value) { file("VERSION.txt").writeText(value) }
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



plugins {
    kotlin("multiplatform") version libs.versions.kotlin.get() apply true
    kotlin("native.cocoapods") version libs.versions.kotlin.get()
    id("com.android.library") version libs.versions.androidGradlePlugin.get() apply true
    id("org.jetbrains.kotlin.android") version libs.versions.kotlin.get() apply false
    id("org.jetbrains.kotlin.plugin.serialization") version libs.versions.kotlin.get()
    id("maven-publish")
    id("org.jetbrains.dokka") version libs.versions.dokka.get()
    id("com.github.ben-manes.versions") version libs.versions.versionsPlugin.get()
    // id("org.lwjgl.plugin") apply true
    // id("org.jlleitschuh.gradle.ktlint")
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
    compileSdk=35

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

    androidTarget("android") {
        publishLibraryVariants("release", "debug")
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
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
        }
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions.freeCompilerArgs.add("-Xir-minimized-member-names=false")
            }
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
        it.compilerOptions.freeCompilerArgs.add("-Xbinary=preCodegenInlineThreshold=40")
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
            dependsOn(commonTest)
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
        templatesDir = file("docs/dokkaTemplates")
        customStyleSheets.from("docsAssets/zk-docs-styles.css")
        customAssets.from("docsAssets/logo-icon.svg")
        footerMessage.set("© ${getYear()} $zernikalosNameCapital")
    }
}

// Custom Tasks

// Generate a ZVersion.kt file with the current version number.
tasks.register("generateVersionConstants") {
    val outputDir = file("src/commonMain/kotlin/zernikalos")
    val outputFile = file("$outputDir/ZVersion.kt")

    inputs.property("version", zernikalosVersion)

    outputs.dir(outputDir)

    doLast {
        outputDir.mkdirs()
        val templateFile = file(".zversion.kt.template")
        val templateContent = templateFile.readText()
        val processedContent = templateContent.replace("\${project.version}", project.version.toString())
        outputFile.writeText(processedContent)
    }
}

// Make sure that the version constants are generated before any compilation task.
tasks.configureEach {
    if (name.startsWith("compile") ||
        name.endsWith("SourcesJar") ||
        name.endsWith("Jar")) {
        dependsOn("generateVersionConstants")
        dependsOn("kotlinUpgradePackageLock")
    }
}

tasks.register<Copy>("generateNpmrc") {
    from(".npmrc.template")
    into(layout.buildDirectory.dir("js").get().toString())
    rename(".npmrc.template", ".npmrc")
    filter { line ->
        line.replace("\${GITHUB_USER}", publishUser)
            .replace("\${GITHUB_TOKEN}", publishAccessToken)
    }
}

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

tasks.register("updateVersion") {
    description = "Generates all version-dependent files (constants, podspec, etc.). Run setVersion first."
    group = "versioning"

    // This task acts as an aggregator. It will use the version currently in VERSION.txt.
    finalizedBy("generateVersionConstants", "podspec", "jsBrowserDistribution")
}

tasks.register<Exec>("releaseCommit") {
    description = "Stages all changes, creates a release commit, and tags it. Format: 'release: 🚀 vX.Y.Z'"
    group = "versioning"

    // Ensure this runs after the version is updated and files are generated.
    dependsOn("updateVersion")

    workingDir = rootDir
    commandLine(
        "sh",
        "-c",
        "git add . && git commit -m \"release: 🚀 v$zernikalosVersion\" && git tag -a \"v$zernikalosVersion\" -m \"Release v$zernikalosVersion\""
    )
}

tasks.withType<org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest>().configureEach {
    testLogging {
        events("passed", "skipped", "failed")
    }
}
