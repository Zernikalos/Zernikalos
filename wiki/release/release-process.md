# Zernikalos Release Process

This page documents the complete release process of the Zernikalos engine, from preparation to automatic publication.

## Process Overview

The Zernikalos release system is designed to completely automate the process of publishing new versions. The recommended approach uses the Zernikalos Manager (`zmanager.py`) for local development and testing, with automatic CI/CD pipeline activation when version tags are pushed.

## Quick Start

The easiest way to create and publish a new version:

```bash
# Complete release (version + publish)
python3 scripts/zmanager.py release 1.2.3

# Or with auto-versioning from Conventional Commits
python3 scripts/zmanager.py release --auto

# Or step by step:
python3 scripts/zmanager.py version 1.2.3  # Create version
python3 scripts/zmanager.py publish --all  # Publish all artifacts
```

For detailed command reference, see [Zernikalos Manager Reference](zmanager-reference.md).

## Automated Release Flow

### 1. Pipeline Activation

The process starts automatically when a version tag is pushed to the main branch (format `vX.Y.Z`):

```bash
git tag -a "v1.2.3" -m "Release v1.2.3"
git push origin main --tags
```

Alternatively, when using Zernikalos Manager, the tag and push are handled automatically:

```bash
python3 scripts/zmanager.py release 1.2.3
```

### 2. Release Workflow (.github/workflows/release.yml)

The workflow runs automatically when a version tag is pushed:

#### Job 1: Build and Publish Android
- **Trigger**: Version tags (`v*.*.*`) or manual execution
- **Objective**: Build and publish Android artifacts (Maven)
- Builds the complete project using Gradle
- Publishes Maven artifacts for debug and release
- Uses GitHub Packages credentials
- **Platform**: Ubuntu with Android SDK 34.0.0
- Uploads build artifacts for JavaScript job

#### Job 2: Publish JavaScript
- **Depends on**: Build and Publish Android job
- Publishes NPM package to GitHub Packages using **workspace support**
- **Secure authentication** via environment variables (no `.npmrc` files)
- **Automatic filtering** excludes test packages
- Automatic configuration of npm.pkg.github.com registry
- Downloads build artifacts from Android job

### Flow Diagram

```
Tag vX.Y.Z → Release Workflow → Automatic Publication
     ↓              ↓                    ↓
  Git Push    Build & Publish        Maven + NPM
              (Android + JS)      GitHub Packages
```

## Creating a Release

### Recommended: Using Zernikalos Manager

Zernikalos Manager provides the simplest and most reliable way to create releases:

```bash
# Complete release (version + publish)
python3 scripts/zmanager.py release 1.2.3

# Auto-versioned release (calculates version from Conventional Commits)
python3 scripts/zmanager.py release --auto

# Step by step (for more control)
python3 scripts/zmanager.py version 1.2.3
python3 scripts/zmanager.py publish --all
```

**Features:**
- ✅ **Complete automation** - Handles versioning, building, and publishing
- ✅ **Auto-versioning** - Calculates version from Conventional Commits
- ✅ **Secure authentication** - Uses environment variables (no files)
- ✅ **Workspace support** - Efficient NPM publishing with filtering
- ✅ **Error handling** - Robust error detection and recovery
- ✅ **Cross-platform** - Works on Windows, macOS, and Linux

For complete command documentation, see [Zernikalos Manager Reference](zmanager-reference.md).

### Alternative: Using Gradle Commands

For advanced users who prefer manual control over the release process:

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

For detailed Gradle task documentation, see [Gradle Tasks](gradle-tasks.md).

### Alternative: Manual Step by Step

For complete manual control:

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

## Local Testing Before Push

Before pushing a release, you can test locally:

```bash
# Create local version without pushing (no CI/CD trigger)
python3 scripts/zmanager.py version 1.2.3 --no-push

# Test publishing locally
python3 scripts/zmanager.py publish --all

# Check project status
python3 scripts/zmanager.py status

# View detailed package information
python3 scripts/zmanager.py info
```

## Release Verification

### 1. GitHub Actions

After pushing a release tag:

- Monitor execution in the "Actions" tab: https://github.com/Zernikalos/Zernikalos/actions
- Verify that the release workflow completes successfully
- Workflow: `.github/workflows/release.yml`

### 2. Published Artifacts

Verify that artifacts were published:

- **Maven**: Verify in repository's GitHub Packages
  - Location: `https://maven.pkg.github.com/Zernikalos/Zernikalos`
- **NPM**: Verify in `@zernikalos/zernikalos` in GitHub Packages
  - Location: `https://npm.pkg.github.com/@zernikalos/zernikalos`
- **GitHub Release**: Created automatically with the tag

### 3. Local Verification

Verify the release locally:

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
- See [Zernikalos Manager Reference](zmanager-reference.md#prerequisites) for authentication details

### Dependencies

- **Android**: Android SDK 34.0.0 or higher
- **iOS**: Xcode installed (for local builds)
- **Web**: Node.js 22+ and npm
- **Gradle**: Latest compatible version
- **Python**: Python 3.6+ (for Zernikalos Manager)

### Troubleshooting

- **Failed build**: Check logs in GitHub Actions
- **Publication failed**: Verify credentials and permissions
- **Duplicate version**: Remove local and remote tag before retrying
- **Version conflicts**: Use `python3 scripts/zmanager.py version --show-next` to check next version

## Related Documentation

- [Zernikalos Manager Reference](zmanager-reference.md) - Complete command reference
- [Gradle Tasks](gradle-tasks.md) - Manual Gradle task documentation

## Flow Summary

The system is designed to be robust and automated, minimizing manual intervention while maintaining flexibility for custom releases when needed.

```
User → Zernikalos Manager → Git Tag → CI/CD Workflow → Published Artifacts
  ↓              ↓              ↓              ↓                ↓
Local      Version &      Automated     Build &        Maven + NPM
Testing    Publishing     Tag Push      Publish        Packages
```
