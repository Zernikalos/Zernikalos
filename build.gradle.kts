
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
        targetSdk=33

        version="0.0.1"

        testInstrumentationRunner="androidx.test.runner.AndroidJUnitRunner"
        // consumerProguardFiles="consumer-rules.pro"
    }
    lint {
        abortOnError=false
    }

}

val lwjglVersion = "3.3.1"
val lwjglNatives = "natives-linux"

kotlin {
    android {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    jvm {
//        compilations.all {
//            kotlinOptions.jvmTarget = "1.8"
//        }
        // withJava()
//        testRuns["test"].executionTask.configure {
//            useJUnitPlatform()
//        }
    }

    js(IR) {
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

    
    sourceSets {
        all {
            languageSettings.optIn("zernikalos.OptInAnnotation")
            languageSettings.optIn("kotlin.js.ExperimentalJsExport")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }

        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.5.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
            }
        }


        val jvmMain by getting {
            kotlin.srcDir("src/jvmMain/kotlin")

            dependencies {
//                lwjgl {
//                    // implementation(Lwjgl.Preset.minimalOpenGL.)
//                    version= "3.3.1"
//                }
                // implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

                implementation("org.lwjgl:lwjgl:$lwjglVersion")
                implementation("org.lwjgl:lwjgl-assimp:$lwjglVersion")
                implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion")
                implementation("org.lwjgl:lwjgl-openal:$lwjglVersion")
                implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion")
                implementation("org.lwjgl:lwjgl-stb:$lwjglVersion")

                runtimeOnly("org.lwjgl:lwjgl:$lwjglVersion")
                runtimeOnly("org.lwjgl:lwjgl-assimp:$lwjglVersion")
                runtimeOnly("org.lwjgl:lwjgl-glfw:$lwjglVersion")
                runtimeOnly("org.lwjgl:lwjgl-openal:$lwjglVersion")
                runtimeOnly("org.lwjgl:lwjgl-opengl:$lwjglVersion")
                runtimeOnly("org.lwjgl:lwjgl-stb:$lwjglVersion")
            }
        }
        // val jvmTest by getting
        val androidMain by getting {
            kotlin.srcDir("src/androidMain/kotlin")
        }
        // val androidTest by getting
        val jsMain by getting
    }
}
