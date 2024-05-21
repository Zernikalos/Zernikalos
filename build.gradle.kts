/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl

plugins {
    kotlin("multiplatform") apply true
    id("com.android.library") apply true
    id("org.jetbrains.kotlin.android") apply false
    id("org.jetbrains.kotlin.plugin.serialization")
    // id("org.lwjgl.plugin") apply true
    // id("org.jlleitschuh.gradle.ktlint")
}

group = "zernikalos"
version = "0.0.1"

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    google()
}

android {
    namespace="io.zernikalos"
    compileSdk=33

    defaultConfig {
        minSdk=24

        version="0.0.1"

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

    androidTarget {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    js(IR) {
        moduleName = "@zernikalos/zernikalos"
        compilations["main"].packageJson {
            customField("author", "Aarón Negrín")
            customField("description", "Zernikalos Game Engine for the browser")
            customField("license", "MPL v2.0")
            customField("types", "kotlin/@zernikalos/zernikalos.d.ts")
        }
        browser {
            binaries.executable()
            @OptIn(ExperimentalDistributionDsl::class)
            distribution {
                outputDirectory = File("$projectDir/output/")
            }
            commonWebpackConfig {
                output?.libraryTarget = "umd"
                output?.library = "zernikalos"
                mode = org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.DEVELOPMENT
                sourceMaps = true
            }
            generateTypeScriptDefinitions()
        }
    }
//    val hostOs = System.getProperty("os.name")
//    val isMingwX64 = hostOs.startsWith("Windows")
//    val nativeTarget = when {
//        hostOs == "Mac OS X" -> macosX64("native")
//        hostOs == "Linux" -> linuxX64("native")
//        isMingwX64 -> mingwX64("native")
//        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
//    }

    val appleFrameworkBaseName = "Zernikalos"

    val xcf = XCFramework(appleFrameworkBaseName)

    macosArm64() {
        binaries.framework {
            isStatic = true
            baseName = appleFrameworkBaseName
            binaryOption("bundleId", "io.zernikalos")
            xcf.add(this)
        }
    }

    iosArm64() {
        binaries.framework {
            isStatic = true
            baseName = appleFrameworkBaseName
            binaryOption("bundleId", "io.zernikalos")
            xcf.add(this)
        }
    }

    iosSimulatorArm64() {
        binaries.framework {
            isStatic = true
            baseName = appleFrameworkBaseName
            binaryOption("bundleId", "io.zernikalos")
            xcf.add(this)
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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.6.3")
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

//        val iosMain by getting {
//            dependsOn(metalMain)
//            kotlin.srcDir("src/iosMain/kotlin")
//        }
//
//        val iosSimulatorArm64Main by getting {
//            dependsOn(iosMain)
//        }

    }
}
