/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    id("zernikalos.constants")
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.cocoapods)
    alias(libs.plugins.kotlinSerialization)
    id("maven-publish")
    id("com.github.ben-manes.versions") version libs.versions.versionsPlugin.get()
}

val zernikalosGroup = project.extra["zernikalosGroup"] as String
val zernikalosName = project.extra["zernikalosName"] as String
val zernikalosNamedGroup = project.extra["zernikalosNamedGroup"] as String
val zernikalosNameCapital = project.extra["zernikalosNameCapital"] as String
val zernikalosDescription = project.extra["zernikalosDescription"] as String
val zernikalosAuthorName = project.extra["zernikalosAuthorName"] as String
val zernikalosLicense = project.extra["zernikalosLicense"] as String
val zernikalosSiteUrl = project.extra["zernikalosSiteUrl"] as String
val githubOwner = project.extra["githubOwner"] as String
val githubRepo = project.extra["githubRepo"] as String
val githubPackagesMavenUrl = project.extra["githubPackagesMavenUrl"] as String
val githubPackagesNpmRegistry = project.extra["githubPackagesNpmRegistry"] as String
val publishUser = project.extra["publishUser"] as String
val publishAccessToken = project.extra["publishAccessToken"] as String
val zernikalosVersion = project.extra["zernikalosVersion"] as String

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

    android {
        namespace = zernikalosGroup
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        version = zernikalosVersion

        withJava()
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        lint {
            abortOnError = false
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
                outputFileName = "${zernikalosName}.js"
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
            kotlin.srcDir("src/jsMain/kotlin")
            dependencies {
                implementation(devNpm("string-replace-loader", "3.2.0"))
            }
            dependsOn(webgpuMain)
        }

        jsTest {
        }

        macosArm64Main {
            dependsOn(metalMain)
            kotlin.srcDir("src/macosMain/kotlin")
        }

        iosMain {
            dependsOn(metalMain)
            kotlin.srcDir("src/iosMain/kotlin")
        }
    }
}

apply(plugin = "zernikalos.dokka-conventions")
apply(plugin = "zernikalos.release-conventions")

tasks.register<Copy>("generateNpmrc") {
    from(".npmrc.template")
    into(layout.buildDirectory.dir("js").get().toString())
    rename(".npmrc.template", ".npmrc")
    filter { line ->
        line.replace("\${GITHUB_USER}", publishUser)
            .replace("\${GITHUB_TOKEN}", publishAccessToken)
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest>().configureEach {
    testLogging {
        events("passed", "skipped", "failed")
    }
}
