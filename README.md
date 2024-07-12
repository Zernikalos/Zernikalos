<h2 align="center">Zernikalos Engine</h2>

<div align="center">
<img src="assets/zklogo.svg" alt="Logo" width="124" height="124">


  <p align="center">
    Zernikalos is a lightweight and fast game engine developed entirely in Kotlin for Android, iOS, and Web. Built with a simple yet powerful API, it facilitates efficient game development, ensuring a seamless experience across platforms.

  </p>
</div>

## Project description

Zernikalos is a lightweight and fast game engine developed entirely in Kotlin for Android, iOS, and Web. Built with a simple yet powerful API, it facilitates efficient game development, ensuring a seamless experience across platforms.

## Building the project

For building the project across all platforms, one simple command is needed: `gradle build`.

Just enter this command into your terminal at the project's root directory. The `gradle build` command compiles the source code, runs the tests, and produces the application JAR files.

Make sure you have Gradle installed on your system and properly set up in your environment variables before running the command.

For more information on how to use Gradle for building your projects, you can refer to its [official documentation](https://docs.gradle.org/current/userguide/userguide.html).

### Build the project for Android



### Build the project for iOS

#### Generating a XCFramework

To assemble the project specifically for iOS, the `gradle assembleZernikalosXCFramework` command is used, which creates an XCFramework for the iOS platform. This command should be run in your terminal at the project's root directory.

It's important to note that in order to build your project for iOS, you must have Xcode installed on your machine. Xcode contains the tools needed to compile your project for the iOS platform. It is recommended to use the latest version of Xcode, which can be downloaded from the [Apple Developer website](https://developer.apple.com/xcode/) or directly from the App Store.

### Build the project for Web

In order to assemble the project for web platforms, the `gradle jsBrowserWebpack` command is used. This command prepares the JavaScript and its dependencies to run in a browser. Again, this command should be run in your terminal at the project's root directory.

Please note, this build relies on the configuration provided in your webpack.config.js file.

[//]: # (## Contributors)

[//]: # ()
[//]: # ([How others can contribute to your project, if applicable])

## Getting started

### For Android

### For iOS

### For Web

## License

This project is licensed under the Mozilla Public License 2.0 (MPL 2.0). For more details, please see the [LICENSE](LICENSE) file in this repository.