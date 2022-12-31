plugins {
    val kotlinVersion = "1.7.20"

    kotlin("multiplatform") version kotlinVersion
    id("com.android.library") version "7.3.1" apply true
    id("org.jetbrains.kotlin.android") version kotlinVersion apply false
    id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion apply true
}

group = "mr.robotto"
version = "0.0.1"

ext {
    version = "1.8.0"
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

android {
    namespace="mr.robotto"
    compileSdk=33

    defaultConfig {
        minSdk=24
        targetSdk=33

        version="0.0.1"

        testInstrumentationRunner="androidx.test.runner.AndroidJUnitRunner"
        // consumerProguardFiles="consumer-rules.pro"
    }
    lint {
        abortOnError=false
    }

}

kotlin {
    android {

    }

    /* jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        // withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    } */

    js(IR) {
        browser {
            binaries.executable()
            distribution {
                directory = File("$projectDir/output/")
            }
            commonWebpackConfig {
                output?.libraryTarget = "umd"
                output?.library = "mrrobotto"
                mode = org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.DEVELOPMENT
            }
        }
    }
    // val hostOs = System.getProperty("os.name")
    // val isMingwX64 = hostOs.startsWith("Windows")
    /* val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    } */

    
    sourceSets {
        all {
            languageSettings.optIn("mr.robotto.OptInAnnotation")
            languageSettings.optIn("kotlin.js.ExperimentalJsExport")
        }

        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.4.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
            }
        }
        //val commonTest by getting {
        //    dependencies {
        //        implementation(kotlin("test"))
        //    }
        //}
        // val jvmMain by getting
        // val jvmTest by getting
        val androidMain by getting {

        }
        // val androidTest by getting
        val jsMain by getting
        // val jsTest by getting
        // val nativeMain by getting
        // val nativeTest by getting
    }
}
