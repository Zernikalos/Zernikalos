# Gradle Release Tasks

This document describes the Gradle tasks available for manual release management. These tasks are intended for advanced users who prefer manual control over the release process.

For most users, the recommended approach is to use the [Zernikalos Manager](zmanager-reference.md), which automates these tasks.

## Overview

Gradle provides several custom tasks for version management and release preparation. These tasks can be used independently or as part of a manual release process.

## Version Management Tasks

### `setVersion`

Sets the project version in `VERSION.txt`.

**Usage:**
```bash
./gradlew setVersion -PnewVersion=1.2.3
```

**Parameters:**
- `-PnewVersion=X.Y.Z` (required): The new version number in semantic versioning format

**Behavior:**
- Updates `VERSION.txt` with the new version
- Does NOT automatically generate version-dependent files
- Does NOT create commits or tags

**Example:**
```bash
./gradlew setVersion -PnewVersion=0.6.1
```

**Note:** After running `setVersion`, you must run `updateVersion` to generate version-dependent files.

### `updateVersion`

Generates all version-dependent files using the current version from `VERSION.txt`.

**Usage:**
```bash
./gradlew updateVersion
```

**Behavior:**
- Generates `ZVersion.kt` with current version
- Updates iOS podspec with current version
- Generates JS distribution files with current version
- Does NOT update `VERSION.txt` (use `setVersion` first)
- Does NOT create commits or tags

**Generated Files:**
- `src/commonMain/kotlin/zernikalos/ZVersion.kt` - Kotlin version constants
- iOS podspec version
- JavaScript package.json version

**Example:**
```bash
# First set the version
./gradlew setVersion -PnewVersion=0.6.1

# Then generate version-dependent files
./gradlew updateVersion
```

### `generateVersionFile`

Creates `ZVersion.kt` file with current version number. This task runs automatically before compilation tasks.

**Usage:**
```bash
./gradlew generateVersionFile
```

**Behavior:**
- Creates `ZVersion.kt` with current version
- Location: `src/commonMain/kotlin/zernikalos/ZVersion.kt`
- Reads version from `VERSION.txt` or `-Pversion` parameter
- Runs automatically before compilation tasks (via task dependencies)

**Note:** This task is part of the `updateVersion` task chain and typically does not need to be run manually.

### `printVersion`

Prints current project version information. Useful for debugging and CI.

**Usage:**
```bash
./gradlew printVersion
```

**Behavior:**
- Prints version from `VERSION.txt`
- Prints version from `-Pversion` parameter (if provided)
- Prints effective version (the one used by build)
- Does NOT modify any files

**Example:**
```bash
./gradlew printVersion
# Output:
# ============================================================
# 📦 Project Version Information
# ============================================================
# Version from VERSION.txt: 0.6.0
# Version from -Pversion param: (not provided)
# Effective version (used by build): 0.6.0
# ============================================================
```

### `releaseCommit`

Creates a release commit and Git tag.

**Usage:**
```bash
./gradlew releaseCommit
```

**Behavior:**
- Stages all changes
- Creates commit with format: `release: 🚀 vX.Y.Z`
- Creates annotated Git tag: `vX.Y.Z`
- Depends on `updateVersion` (runs it automatically if needed)
- Does NOT push changes (you must push manually)

**Requirements:**
- Must be run after `setVersion` and `updateVersion`
- Git must be configured and repository must be clean or have staged changes

**Example:**
```bash
# Complete manual release process
./gradlew setVersion -PnewVersion=0.6.1
./gradlew updateVersion
./gradlew releaseCommit

# Then push manually
git push origin main --tags
```

**Note:** This task uses `git add .` to stage all changes. Ensure you want all changes included in the release commit.

## Complete Manual Release Process

Here is a complete example of using Gradle tasks for a manual release:

### Step 1: Set New Version
```bash
./gradlew setVersion -PnewVersion=0.6.1
```

### Step 2: Generate Version Files
```bash
./gradlew updateVersion
```

### Step 3: Verify Changes
```bash
# Check version in VERSION.txt
cat VERSION.txt

# Check generated version constants
cat src/commonMain/kotlin/zernikalos/ZVersion.kt

# Check package.json versions (after build)
cat build/js/packages/@zernikalos/zernikalos/package.json | grep version
```

### Step 4: Create Release Commit and Tag
```bash
./gradlew releaseCommit
```

### Step 5: Push to Trigger CI/CD
```bash
git push origin main --tags
```

## Task Dependencies

Understanding task dependencies helps when troubleshooting:

- `releaseCommit` → depends on → `updateVersion`
- `updateVersion` → finalized by → `generateVersionFile`
- Compilation tasks → depend on → `generateVersionFile`

## Comparison with Zernikalos Manager

| Feature | Gradle Tasks | Zernikalos Manager |
|---------|--------------|-------------------|
| Version management | Manual (multiple commands) | Automated (single command) |
| Auto-versioning | ❌ | ✅ (from Conventional Commits) |
| Git operations | Manual | Automated |
| Validation | Basic | Comprehensive |
| Error handling | Basic | Advanced |
| CI/CD integration | Manual push | Automatic |

**Recommendation:** Use Zernikalos Manager for most cases. Use Gradle tasks only when you need fine-grained control over the release process.

## Troubleshooting

### Version not updating
- Ensure `-PnewVersion` parameter is correct format (X.Y.Z)
- Check that `VERSION.txt` was actually updated
- Verify no `-Pversion` parameter overrides it

### Files not generated
- Run `updateVersion` after `setVersion`
- Check build logs for errors
- Verify Gradle wrapper is executable

### Release commit fails
- Ensure Git is configured
- Check that you're in the repository root
- Verify `updateVersion` completed successfully
- Check for uncommitted changes or conflicts

### Tag already exists
- Remove existing tag: `git tag -d vX.Y.Z`
- Remove remote tag: `git push origin :refs/tags/vX.Y.Z`
- Retry the release process

## Related Documentation

- [Release Process](release-process.md) - Complete release workflow
- [Zernikalos Manager Reference](zmanager-reference.md) - Automated release tool
