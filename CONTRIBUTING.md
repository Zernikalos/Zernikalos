# Contributing to Zernikalos

¡Gracias por tu interés en contribuir a Zernikalos Engine! 🚀

## 🎯 How to Contribute

### Reporting Issues
- Use the [GitHub Issues](https://github.com/Zernikalos/Zernikalos/issues) page
- Include detailed information about your environment
- Provide steps to reproduce the issue
- Include error messages and logs when possible

### Suggesting Features
- Open a [Feature Request](https://github.com/Zernikalos/Zernikalos/issues/new?template=feature_request.md) issue
- Describe the use case and expected behavior
- Consider if it aligns with the project's goals

### Code Contributions
1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feat/amazing-feature`)
3. **Make** your changes
4. **Test** your changes thoroughly
5. **Commit** your changes following our [commit format](#commit-format)
6. **Push** to the branch (`git push origin feat/amazing-feature`)
7. **Open** a Pull Request

### Commit Format

We use a structured commit format with emojis for better readability:

```bash
git commit -m "feat: 🎸 Add new rendering component"
git commit -m "fix: 🐛 Resolve memory leak in texture loading"
git commit -m "docs: 📚 Update API documentation"
```

**Commit Types:**
- `feat: 🎸` - New feature
- `fix: 🐛` - Bug fix
- `docs: 📚` - Documentation
- `style: 🎨` - Style changes
- `refactor: 💡` - Code refactoring
- `test: ✅` - Tests addition or correction
- `chore: 🤖` - Maintenance tasks

**Format:** `{type}: {emoji} {message}`

## 🛠️ Development Setup

### Prerequisites
- **Gradle**: Latest version
- **Kotlin**: Multiplatform support
- **Android**: Android SDK (for Android builds)
- **iOS**: Xcode (for iOS builds) - macOS only
- **Web**: Node.js (for web builds)

### Building
```bash
# Build all platforms
./gradlew build

# Platform-specific builds
./gradlew assembleAndroidRelease
./gradlew assembleZernikalosXCFramework
./gradlew jsBrowserWebpack
```

### Testing
```bash
# Run all tests
./gradlew test

# Platform-specific tests
./gradlew androidTest
./gradlew jsTest
```

## 📋 Code Style Guidelines

### Kotlin
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use 4 spaces for indentation
- Use meaningful variable and function names
- Add KDoc comments for public APIs

### Project Structure
- Keep platform-specific code in respective source sets
- Use common interfaces for cross-platform functionality
- Follow the existing package structure
- Maintain separation of concerns

## 🚀 Release Process

### For Contributors
- Ensure all tests pass
- Update documentation if needed
- Follow the [Release Process](README.md#-release-process) in the README

### For Maintainers
- Review and merge PRs
- Run the release workflow
- Update version numbers
- Create GitHub releases

## 📚 Documentation

- Update README.md for user-facing changes
- Add KDoc comments for new APIs
- Update relevant documentation in the `docs/` folder
- Include examples when adding new features

## 🤝 Community

- Be respectful and inclusive
- Help other contributors
- Join discussions in issues and PRs
- Share your knowledge and experience

## 📄 License

By contributing to Zernikalos, you agree that your contributions will be licensed under the [Mozilla Public License 2.0](LICENSE).

---

**Thank you for contributing to Zernikalos Engine!** 🎮✨
