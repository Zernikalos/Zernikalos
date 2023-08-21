
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
    namespace="com.zernikalos"
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
    androidTarget {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    js(IR) {
        moduleName = "@zernikalos/zernikalos"
        compilations["main"].packageJson {
            customField("author", "Aarón Negrín")
            customField("description","Zernikalos Game Engine for the browser")
            customField("license", "MPL v2.0")
        }
        browser {
            binaries.executable()
            distribution {
                directory = File("$projectDir/output/")
            }
            commonWebpackConfig {
                output?.libraryTarget = "umd"
                output?.library = "zernikalos"
                mode = org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.DEVELOPMENT
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

    macosArm64("native") {
        binaries.framework {
            isStatic = true
            baseName="Zernikalos"
            binaryOption("bundleId", "com.zernikalos")
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

        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.5.1")
            }
        }

        val oglMain by creating {
            kotlin.srcDir("src/oglMain/kotlin")
            dependsOn(commonMain)
        }

        val androidMain by getting {
            kotlin.srcDir("src/androidMain/kotlin")
            dependsOn(oglMain)
        }

        val jsMain by getting {
            languageSettings.optIn("zernikalos.OptInAnnotation")
            dependsOn(oglMain)
        }

        val nativeMain by getting {
            languageSettings.optIn("zernikalos.OptInAnnotation")
        }

    }
}
