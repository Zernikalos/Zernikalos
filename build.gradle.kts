/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import java.net.URL
import java.time.Year

// Constants
val zernikalosGroup = "dev.zernikalos"
val zernikalosName = "zernikalos"
val zernikalosNamedGroup = "${zernikalosGroup}.$zernikalosName"
val zernikalosNameCapital = "Zernikalos"
val zernikalosVersion = "0.0.1"
val zernikalosDescription = "Zernikalos Game Engine"

val zernikalosAuthorName = "Aarón Negrín"
val zernikalosLicense = "MPL v2.0"
val zernikalosSiteUrl = "https://zernikalos.dev"

plugins {
    kotlin("multiplatform") version libs.versions.kotlin.get() apply true
    kotlin("native.cocoapods") version libs.versions.kotlin.get()
    id("com.android.library") version libs.versions.androidGradlePlugin.get() apply true
    id("org.jetbrains.kotlin.android") version libs.versions.kotlin.get() apply false
    id("org.jetbrains.kotlin.plugin.serialization") version libs.versions.kotlin.get()
    id("maven-publish")
    id("org.jetbrains.dokka") version libs.versions.dokka.get() apply true
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

android {
    namespace=zernikalosGroup
    compileSdk=33

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
            compilerOptions.configure {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }

    androidTarget("android") {
        publishLibraryVariants("release", "debug")

        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    js(IR) {
        moduleName = "@zernikalos/zernikalos"
        compilations["main"].packageJson {
            customField("author", zernikalosAuthorName)
            customField("description", zernikalosDescription)
            customField("license", zernikalosLicense)
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
//            @OptIn(ExperimentalDistributionDsl::class)
//            distribution {
//                outputDirectory = File("$projectDir/output/")
//            }
            commonWebpackConfig {
                output?.libraryTarget = "umd"
                output?.library = zernikalosName
                mode = org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.DEVELOPMENT
                sourceMaps = true
            }
            generateTypeScriptDefinitions()
        }
    }

    val xcf = XCFramework(zernikalosNameCapital)
    val appleTargets = listOf(macosArm64(), iosArm64(), iosSimulatorArm64())

    appleTargets.forEach {
        it.binaries.framework {
            isStatic = true
            baseName = zernikalosNameCapital
            binaryOption("bundleId", zernikalosNamedGroup)
            binaryOption("bundleVersion", zernikalosVersion)
            debuggable = true
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
//            languageSettings {
//                languageVersion = "2.0"
//            }
            languageSettings.optIn("zernikalos.OptInAnnotation")
            languageSettings.optIn("kotlin.js.ExperimentalJsExport")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }

        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.serialization.protobuf)
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

        androidMain {
            kotlin.srcDir("src/androidMain/kotlin")
            dependsOn(oglMain)
        }

        jsMain {
            // Required for compatibility with zdebugger (see webpack file)
            dependencies {
                implementation(npm("ua-parser-js","1.0.37"))
                implementation(devNpm("string-replace-loader", "3.1.0"))
            }
            dependsOn(oglMain)
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

tasks.withType<DokkaTask>().configureEach {
    val dokkaBaseConfiguration = """
    {
      "customAssets": ["${file("docsAssets/logo-icon.svg")}"],
      "customStyleSheets": ["${file("docsAssets/zk-docs-styles.css")}"],
      "footerMessage": "© ${getYear()} $zernikalosNameCapital"
    }
    """
    pluginsMapConfiguration.set(
        mapOf(
            // fully qualified plugin name to json configuration
            "org.jetbrains.dokka.base.DokkaBase" to dokkaBaseConfiguration
        )
    )

    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory.set(projectDir.resolve("src"))
            remoteUrl.set(URL("https://github.com/Zernikalos/Zernikalos/tree/main/src"))
            remoteLineSuffix.set("#L")
        }
    }
}

