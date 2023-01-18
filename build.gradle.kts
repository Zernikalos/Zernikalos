plugins {
    kotlin("multiplatform") apply true
    id("com.android.library") apply true
    id("org.jetbrains.kotlin.android") apply false
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jlleitschuh.gradle.ktlint")
}

group = "mr.robotto"
version = "0.0.1"

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    google()
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

val lwjglVersion = "3.3.1"

val lwjglNatives = Pair(
    System.getProperty("os.name")!!,
    System.getProperty("os.arch")!!
).let { (name, arch) ->
    when {
        arrayOf("Linux", "FreeBSD", "SunOS", "Unit").any { name.startsWith(it) } ->
            "natives-linux"
        arrayOf("Mac OS X", "Darwin").any { name.startsWith(it) }                ->
            "natives-macos-arm64"
        arrayOf("Windows").any { name.startsWith(it) }                           ->
            "natives-windows"
        else -> throw Error("Unrecognized or unsupported platform. Please set \"lwjglNatives\" manually")
    }
}

kotlin {
    android {

    }

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        // withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

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
        val jvmMain by getting {
            kotlin.srcDir("src/jvmMain/kotlin")

            dependencies {
                // implementation("org.lwjgl:lwjgl-bom:$lwjglVersion")

                implementation("org.lwjgl:lwjgl:$lwjglVersion")
                implementation("org.lwjgl:lwjgl-assimp:$lwjglVersion")
                implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion")
                implementation("org.lwjgl:lwjgl-openal:$lwjglVersion")
                implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion")
                implementation("org.lwjgl:lwjgl-stb:$lwjglVersion")
//
//                runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
//                runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = lwjglNatives)
//                runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
//                runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
//                runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
//                runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)

//                runtimeOnly("org.lwjgl.lwjgl-assimp:$lwjglVersion:$lwjglNatives")
//                runtimeOnly("org.lwjgl.lwjgl-glfw:$lwjglVersion:$lwjglNatives")
//                runtimeOnly("org.lwjgl.lwjgl-openal:$lwjglVersion:$lwjglNatives")
//                runtimeOnly("org.lwjgl.lwjgl-opengl:$lwjglVersion:$lwjglNatives")
//                runtimeOnly("org.lwjgl.lwjgl-stb:$lwjglVersion:$lwjglNatives")
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
