# Zernikalos Release Process

This page documents the complete release process of the Zernikalos engine, from preparation to automatic publication.

## Process Overview

The Zernikalos release system is designed to completely automate the process of publishing new versions. The recommended approach uses Python scripts for local development and testing, with automatic CI/CD pipeline activation when version tags are pushed.

## Recommended Release Process (Python Scripts)

### Quick Release with Python Scripts

The easiest and most reliable way to create and publish new versions:

```bash
# 1. Create new version and publish everything
python3 scripts/publish-all.py --all

# 2. Or step by step:
python3 scripts/version.py 1.2.3  # Create version
python3 scripts/publish-npm.py -a  # Publish NPM packages
python3 scripts/publish-android.py --all-publications  # Publish Android
```

### Python Scripts Features

- ✅ **Complete automation** - Handles versioning, building, and publishing
- ✅ **Secure authentication** - Uses environment variables (no files)
- ✅ **Workspace support** - Efficient NPM publishing with filtering
- ✅ **Interactive mode** - User-friendly prompts and confirmations
- ✅ **Error handling** - Robust error detection and recovery
- ✅ **Cross-platform** - Works on Windows, macOS, and Linux

## Automated Release Flow

### 1. Pipeline Activation
The process starts automatically when a version tag is pushed to the main branch (format `vX.Y.Z`):

```bash
git tag -a "v1.2.3" -m "Release v1.2.3"
git push origin main --tags
```

### 2. Build Workflow (.github/workflows/build.yml)
- **Trigger**: Version tags (`v*.*.*`) or manual execution
- **Objective**: Build the complete project using Gradle
- **Artifacts**: Generates and stores the `build/` directory for later use
- **Platform**: Ubuntu with Android SDK 34.0.0

### 3. Release Workflow (.github/workflows/release.yml)
Runs automatically after a successful build:

#### Android Publication
- Publishes Maven artifacts for debug and release
- Uses GitHub Packages credentials
- Specialized container with Android SDK

#### JavaScript Publication
- Publishes NPM package to GitHub Packages using **workspace support**
- **Secure authentication** via environment variables (no `.npmrc` files)
- **Automatic filtering** excludes test packages
- Automatic configuration of npm.pkg.github.com registry
- Dependency caching for optimization

## Python Release Scripts

### Main Script: `scripts/publish-all.py`

Complete automation for all publishing tasks:

```bash
# Interactive mode (recommended)
python3 scripts/publish-all.py

# Publish everything at once
python3 scripts/publish-all.py --all

# Show project status
python3 scripts/publish-all.py -s
```

### Version Management: `scripts/version.py`

Handles version creation and management:

```bash
# Create new version
python3 scripts/version.py 1.2.3

# Show current version
python3 scripts/version.py --current

# List all versions
python3 scripts/version.py --list
```

### NPM Publishing: `scripts/publish-npm.py`

Specialized tool for NPM package publication:

```bash
# List available packages
python3 scripts/publish-npm.py -l

# Publish all packages
python3 scripts/publish-npm.py -a

# Publish specific package
python3 scripts/publish-npm.py zernikalos
```

#### Features:
- **Secure authentication** via environment variables (no `.npmrc` files)
- **Workspace support** for efficient publishing
- **Automatic filtering** excludes test packages
- Dependency validation (npm, Node.js)
- Automatic GitHub Packages registry configuration

### Android Publishing: `scripts/publish-android.py`

Handles Android artifact publication:

```bash
# Interactive mode
python3 scripts/publish-android.py

# Publish all publications (recommended)
python3 scripts/publish-android.py --all-publications

# Show Maven coordinates
python3 scripts/publish-android.py -i
```

## Manual Step-by-Step Release (Gradle Commands)

*This section is for advanced users who prefer manual control over the release process.*

### Gradle Versioning Tasks

#### `setVersion`
```bash
./gradlew setVersion -PnewVersion=1.2.3
```
- Updates `VERSION.txt` with the new version
- Requires `-PnewVersion=X.Y.Z` parameter

#### `updateVersion`
```bash
./gradlew updateVersion
```
- Generates version-dependent files
- Includes: Kotlin constants, iOS podspec, JS distribution
- Runs automatically after `setVersion`

#### `generateVersionConstants`
- Creates `ZVersion.kt` with current version
- Runs automatically before compilation
- Location: `src/commonMain/kotlin/zernikalos/ZVersion.kt`

#### `releaseCommit`
```bash
./gradlew releaseCommit
```
- Creates release commit with format: `release: 🚀 vX.Y.Z`
- Generates annotated Git tag
- Depends on `updateVersion` for updated files

## Creating a New Local Version

### Recommended: Using Python Scripts

```bash
# Quick version creation and publishing
python3 scripts/version.py 1.2.3
python3 scripts/publish-all.py --all

# Or interactive mode
python3 scripts/publish-all.py
```

### Manual: Using Gradle Commands

#### 1. **Set New Version**
```bash
# Set the new version (e.g., 1.2.3)
./gradlew setVersion -PnewVersion=1.2.3
```

#### 2. **Generate Version Files**
```bash
# Generate all version-dependent files
./gradlew updateVersion
```

#### 3. **Verify Changes**
```bash
# Check version in VERSION.txt
cat VERSION.txt

# Check generated version constants
cat src/commonMain/kotlin/zernikalos/ZVersion.kt

# Check package.json versions
cat build/js/packages/@zernikalos/zernikalos/package.json | grep version
```

#### 4. **Create Release Commit and Tag**
```bash
# Create commit and tag
./gradlew releaseCommit
```

#### 5. **Push to Trigger CI/CD (Optional)**
```bash
# Push to trigger automated build and publish
git push origin main --tags
```

### Local Testing Before Push

```bash
# Test NPM publishing locally
python3 scripts/publish-npm.py -l  # List packages
python3 scripts/publish-npm.py zernikalos  # Publish specific package

# Test Android publishing locally
python3 scripts/publish-android.py -i  # Show info
python3 scripts/publish-android.py --all-publications  # Publish all

# Test everything locally
python3 scripts/publish-all.py --all
```

## Complete Release Process

### Option 1: Python Scripts (Recommended)
```bash
# Complete automation with Python scripts
python3 scripts/publish-all.py --all

# Or step by step
python3 scripts/version.py 1.2.3
python3 scripts/publish-npm.py -a
python3 scripts/publish-android.py --all-publications
```

### Option 2: Manual Gradle Commands
```bash
# 1. Set new version
./gradlew setVersion -PnewVersion=1.2.3

# 2. Generate version files
./gradlew updateVersion

# 3. Create commit and tag
./gradlew releaseCommit

# 4. Push to trigger CI/CD
git push origin main --tags
```

### Option 3: Manual Step by Step
```bash
# Update VERSION.txt manually
echo "1.2.3" > VERSION.txt

# Generate files
./gradlew updateVersion

# Manual commit and tag
git add .
git commit -m "release: 🚀 v1.2.3"
git tag -a "v1.2.3" -m "Release v1.2.3"
git push origin main --tags
```

## Release Verification

### 1. GitHub Actions
- Monitor execution in the "Actions" tab
- Verify that both workflows (`build` and `release`) complete successfully

### 2. Published Artifacts
- **Maven**: Verify in repository's GitHub Packages
- **NPM**: Verify in `@zernikalos/zernikalos` in GitHub Packages
- **GitHub Release**: Created automatically with the tag

### 3. Local Verification
```bash
# Verify version in code
cat src/commonMain/kotlin/zernikalos/ZVersion.kt

# Verify local tag
git tag -l | grep "v1.2.3"

# Verify Git status
git status
```

## Important Considerations

### Security
- GitHub tokens must have `packages:write` permissions
- Never commit credentials in code
- Use environment variables or command line parameters

### Dependencies
- **Android**: Android SDK 34.0.0 or higher
- **iOS**: Xcode installed (for local builds)
- **Web**: Node.js 22+ and npm
- **Gradle**: Latest compatible version

### Troubleshooting
- **Failed build**: Check logs in GitHub Actions
- **Publication failed**: Verify credentials and permissions
- **Duplicate version**: Remove local and remote tag before retrying

## Flow Summary

```
Tag vX.Y.Z → Build Workflow → Release Workflow → Automatic Publication
     ↓              ↓              ↓                    ↓
  Git Push    Multiplatform    Publication        Maven + NPM
              Construction     Android/JS        GitHub Packages
```

The system is designed to be robust and automated, minimizing manual intervention while maintaining flexibility for custom releases when needed.
