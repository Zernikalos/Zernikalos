# Zernikalos Release Process

This page documents the complete release process of the Zernikalos engine, from preparation to automatic publication.

## Process Overview

The Zernikalos release system is designed to completely automate the process of publishing new versions. When a version tag is created, a CI/CD pipeline is activated that builds, tests, and publishes the engine on all supported platforms.

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
- Publishes NPM package to GitHub Packages
- Automatic configuration of npm.pkg.github.com registry
- Dependency caching for optimization

## Manual Release Tools

### Main Script: `scripts/release.sh`

This script automates the complete local release process:

```bash
# Basic usage
./scripts/release.sh 1.2.3

# Local release without push (useful for testing)
./scripts/release.sh 1.2.3 --no-push
```

#### Script Features:
- **Version validation**: Verifies X.Y.Z format
- **Git verification**: Confirms clean directory
- **Duplicate prevention**: Avoids existing version tags
- **Interactive confirmation**: Shows changes before proceeding
- **Error handling**: Safe exit in case of problems

### NPM Publication Script: `scripts/publish-npm.sh`

Specialized tool for manual NPM package publication:

```bash
# With credentials as parameters
./scripts/publish-npm.sh --user=Zernikalos --token=ghp_xxx

# With environment variables
export GITHUB_USER=Zernikalos
export GITHUB_TOKEN=ghp_xxx
./scripts/publish-npm.sh
```

#### Characteristics:
- Automatic `.npmrc` generation with credentials
- Dependency validation (npm, Node.js)
- Automatic GitHub Packages registry configuration
- Secure access token handling

## Gradle Release Tasks

### Versioning Tasks

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

#### `generateNpmrc`
- Generates `.npmrc` file for NPM authentication
- Uses `.npmrc.template` template
- Replaces `${GITHUB_USER}` and `${GITHUB_TOKEN}` variables

#### `releaseCommit`
```bash
./gradlew releaseCommit
```
- Creates release commit with format: `release: 🚀 vX.Y.Z`
- Generates annotated Git tag
- Depends on `updateVersion` for updated files

## Complete Release Process

### Option 1: Automated (Recommended)
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

### Option 2: Integrated Script
```bash
# Executes the entire process automatically
./scripts/release.sh 1.2.3
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
