/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import com.android.build.api.variant.KotlinMultiplatformAndroidComponentsExtension
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.util.Properties
import javax.inject.Inject

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

abstract class BuildZernikalosVulkanJniLibsTask: DefaultTask() {
    @get:Inject
    abstract val execOperations: ExecOperations

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:InputDirectory
    abstract val sourceDir: DirectoryProperty

    @TaskAction
    fun build() {
        val sdkDir = findAndroidSdkDir()
        val cmake = findCmakeExecutable(sdkDir)
        val ndkDir = findNdkDir(sdkDir)
        val ndkBuild = findNdkBuildExecutable(ndkDir)
        val toolchain = ndkDir.resolve("build/cmake/android.toolchain.cmake")
        val sourceDir = sourceDir.get().asFile
        val generatedJniLibs = outputDir.get().asFile
        val buildRoot = project.layout.buildDirectory.dir("cmake/zernikalos-vulkan").get().asFile
        val shadercSourceDir = ndkDir.resolve("sources/third_party/shaderc")
        val shadercIncludeDir = shadercSourceDir.resolve("libshaderc/include")

        generatedJniLibs.deleteRecursively()

        listOf("arm64-v8a", "x86_64").forEach { abi ->
            val abiBuildDir = buildRoot.resolve(abi)
            val shadercObjDir = buildRoot.resolve("shaderc/obj/$abi")
            val shadercLibsDir = buildRoot.resolve("shaderc/libs/$abi")
            val shadercLib = shadercLibsDir.resolve("c++_static/$abi/libshaderc.a")

            execOperations.exec {
                commandLine(
                    ndkBuild.absolutePath,
                    "-C", shadercSourceDir.absolutePath,
                    "APP_ABI=$abi",
                    "APP_PLATFORM=android-24",
                    "APP_STL=c++_static",
                    "APP_BUILD_SCRIPT=${shadercSourceDir.resolve("Android.mk").absolutePath}",
                    "NDK_PROJECT_PATH=${shadercSourceDir.absolutePath}",
                    "NDK_OUT=${shadercObjDir.absolutePath}",
                    "NDK_LIBS_OUT=${shadercLibsDir.absolutePath}",
                    "libshaderc_combined",
                )
            }
            if (!shadercLib.isFile) {
                error("Expected shaderc static library was not produced: ${shadercLib.absolutePath}")
            }

            execOperations.exec {
                commandLine(
                    cmake.absolutePath,
                    "-S", sourceDir.absolutePath,
                    "-B", abiBuildDir.absolutePath,
                    "-DCMAKE_TOOLCHAIN_FILE=${toolchain.absolutePath}",
                    "-DANDROID_ABI=$abi",
                    "-DANDROID_PLATFORM=android-24",
                    "-DANDROID_STL=c++_static",
                    "-DZK_SHADERC_LIB=${shadercLib.absolutePath}",
                    "-DZK_SHADERC_INCLUDE=${shadercIncludeDir.absolutePath}",
                )
            }
            execOperations.exec {
                commandLine(cmake.absolutePath, "--build", abiBuildDir.absolutePath)
            }

            val nativeLib = abiBuildDir.resolve("libzernikalos_vulkan.so")
            if (!nativeLib.isFile) {
                error("Expected Vulkan native library was not produced: ${nativeLib.absolutePath}")
            }
            val abiOutputDir = generatedJniLibs.resolve(abi)
            abiOutputDir.mkdirs()
            nativeLib.copyTo(abiOutputDir.resolve(nativeLib.name), overwrite = true)
        }
    }

    private fun findAndroidSdkDir(): File {
        val localProperties = project.rootProject.file("local.properties")
        if (localProperties.isFile) {
            val properties = Properties()
            localProperties.inputStream().use(properties::load)
            properties.getProperty("sdk.dir")?.let { return File(it) }
        }
        System.getenv("ANDROID_HOME")?.let { return File(it) }
        System.getenv("ANDROID_SDK_ROOT")?.let { return File(it) }
        error("Android SDK not found. Set sdk.dir in local.properties or ANDROID_HOME.")
    }

    private fun findCmakeExecutable(sdkDir: File): File {
        val cmakeRoot = sdkDir.resolve("cmake")
        val executable = cmakeRoot.listFiles()
            ?.filter { it.isDirectory }
            ?.sortedBy { it.name }
            ?.lastOrNull()
            ?.resolve("bin/cmake")
        if (executable?.isFile == true) return executable
        error("CMake not found under ${cmakeRoot.absolutePath}")
    }

    private fun findNdkDir(sdkDir: File): File {
        val ndkRoot = sdkDir.resolve("ndk")
        val ndkDir = ndkRoot.listFiles()
            ?.filter { it.isDirectory }
            ?.sortedBy { it.name }
            ?.lastOrNull()
        if (ndkDir?.isDirectory == true) return ndkDir
        error("NDK not found under ${ndkRoot.absolutePath}")
    }

    private fun findNdkBuildExecutable(ndkDir: File): File {
        val executableName = if (System.getProperty("os.name").contains("Windows", ignoreCase = true)) {
            "ndk-build.cmd"
        } else {
            "ndk-build"
        }
        val executable = ndkDir.resolve(executableName)
        if (executable.isFile) return executable
        error("ndk-build not found under ${ndkDir.absolutePath}")
    }
}

allprojects {
    group = zernikalosGroup
    version = zernikalosVersion
}

val buildZernikalosVulkanJniLibs = tasks.register<BuildZernikalosVulkanJniLibsTask>("buildZernikalosVulkanJniLibs") {
    outputDir.set(layout.buildDirectory.dir("generated/zernikalosVulkanJniLibs/androidMain"))
    sourceDir.set(layout.projectDirectory.dir("src/androidMain/cpp"))
}

extensions.configure<KotlinMultiplatformAndroidComponentsExtension>("androidComponents") {
    onVariants(selector().all()) { variant ->
        variant.sources.jniLibs?.addGeneratedSourceDirectory(
            buildZernikalosVulkanJniLibs,
            BuildZernikalosVulkanJniLibsTask::outputDir,
        )
    }
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

        val vulkanMain by creating {
            kotlin.srcDir("src/vulkanMain/kotlin")
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
            dependsOn(vulkanMain)
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
