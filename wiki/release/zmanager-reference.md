# Zernikalos Manager Reference

Complete reference documentation for the Zernikalos Manager (`zmanager.py`), a unified management tool for versioning and publishing Zernikalos packages to GitHub Packages.

## Overview

The Zernikalos Manager uses a modular architecture with subcommands for different operations. All functionality is accessed through a single entry point: `scripts/zmanager.py`.

## System Architecture

The Zernikalos Manager uses a modular structure:

```
scripts/
├── zmanager.py              # Main entry point
├── cli/
│   ├── parser.py            # Main argument parser
│   ├── version_cli.py       # Version command handler
│   ├── publisher_cli.py     # Publish command handler
│   └── common_args.py       # Common arguments
├── versioning/
│   ├── version_manager.py   # Version management logic
│   └── types.py             # Version types
├── publishing/
│   ├── publisher_manager.py # Publication orchestration
│   ├── npm_publisher.py     # NPM publishing
│   ├── maven_publisher.py   # Maven publishing
│   └── base_builder.py      # Base builder functionality
├── tools/
│   ├── gradle.py            # Gradle integration
│   ├── git.py               # Git operations
│   ├── npm.py               # npm integration
│   └── conventional_commits.py # Commit analysis
└── common.py                # Common utilities
```

## Available Commands

### 1. `version` - Version Management

Manages version creation and updates with support for auto-versioning from Conventional Commits.

### 2. `publish` - Artifact Publishing

Publishes NPM packages and Maven artifacts to GitHub Packages.

### 3. `release` - Complete Release

Combines versioning and publishing in a single command.

### 4. `status` - Project Status

Shows current project status and available artifacts.

### 5. `info` - Detailed Information

Displays detailed information about packages and artifacts.

## Command Reference

### `version` Command

Creates a new version, updates version-dependent files, and creates a Git commit and tag.

**Syntax:**
```bash
python3 scripts/zmanager.py version [VERSION] [OPTIONS]
```

**Arguments:**
- `VERSION` (optional): Version number in format `X.Y.Z` (e.g., `1.2.3`)

**Options:**
- `--auto`: Automatically calculate version from Conventional Commits
- `--show-next`: Show next calculated version without creating it
- `--no-push`: Create local version without pushing (no CI/CD trigger)

**Examples:**
```bash
# Create new version
python3 scripts/zmanager.py version 1.2.3

# Auto-calculate version from Conventional Commits
python3 scripts/zmanager.py version --auto

# Show next calculated version (without creating)
python3 scripts/zmanager.py version --show-next

# Create local version without pushing
python3 scripts/zmanager.py version 1.2.3 --no-push
```

**Features:**
- ✅ **Auto-versioning** from Conventional Commits
- ✅ **Version validation** and conflict detection
- ✅ **Automatic file generation** (VERSION.txt, ZVersion.kt, etc.)
- ✅ **Git commit and tag** creation
- ✅ **Optional push** for local testing

**What it does:**
1. Verifies you are in the project root directory
2. Checks that Git is available and configured
3. Validates version format (X.Y.Z) or calculates from Conventional Commits
4. Updates VERSION.txt
5. Upgrades Kotlin package lock
6. Generates all version-dependent files (ZVersion.kt, podspec, JS distribution)
7. Creates Git commit and tag
8. Optionally pushes changes to trigger CI/CD

### `publish` Command

Publishes NPM packages and/or Maven artifacts to GitHub Packages.

**Syntax:**
```bash
python3 scripts/zmanager.py publish [OPTIONS]
```

**Options (one required):**
- `--all`: Publish all artifacts (NPM + Maven)
- `--npm`: Publish only NPM packages
- `--maven`: Publish only Maven artifacts (Android)

**Common Options:**
- `--user USER`: GitHub username/organization
- `--token TOKEN`: GitHub access token

**Examples:**
```bash
# Publish all artifacts (NPM + Maven)
python3 scripts/zmanager.py publish --all

# Publish only NPM packages
python3 scripts/zmanager.py publish --npm

# Publish only Maven artifacts
python3 scripts/zmanager.py publish --maven

# With custom credentials
python3 scripts/zmanager.py publish --all --user Zernikalos --token TOKEN
```

**Features:**
- ✅ **Secure authentication** via environment variables (no `.npmrc` files)
- ✅ **Workspace support** for efficient NPM publishing
- ✅ **Automatic filtering** excludes test packages
- ✅ **Selective publishing** (NPM only, Maven only, or all)
- ✅ **Dependency validation** (npm, Node.js, Gradle)
- ✅ **Automatic GitHub Packages** registry configuration

**What it does:**

For NPM:
1. Verifies you are in the project root directory
2. Checks that npm is installed
3. Configures secure authentication via environment variables
4. Builds packages if `build/js` directory doesn't exist
5. Publishes using npm workspace with automatic filtering

For Maven:
1. Verifies you are in the project root directory
2. Checks that Gradle wrapper is available
3. Builds project if `build` directory doesn't exist
4. Publishes all publications to GitHub Packages Maven Repository

### `release` Command

Combines versioning and publishing in a single command.

**Syntax:**
```bash
python3 scripts/zmanager.py release [VERSION] [OPTIONS]
```

**Arguments:**
- `VERSION` (optional): Version number in format `X.Y.Z` (e.g., `1.2.3`)

**Options:**
- `--auto`: Automatically calculate version from Conventional Commits
- `--no-publish`: Only create version, do not publish artifacts

**Common Options:**
- `--user USER`: GitHub username/organization
- `--token TOKEN`: GitHub access token

**Examples:**
```bash
# Complete release (version + publish)
python3 scripts/zmanager.py release 1.2.3

# Auto-versioned release
python3 scripts/zmanager.py release --auto

# Version only (no publish)
python3 scripts/zmanager.py release 1.2.3 --no-publish
```

**Features:**
- ✅ **Complete automation** combining versioning and publishing
- ✅ **Auto-versioning** support
- ✅ **Optional publishing** step
- ✅ **Comprehensive error handling**

**What it does:**
1. Executes version command (with all its steps)
2. Pushes changes to trigger CI/CD
3. Optionally executes publish command if `--no-publish` is not set

### `status` Command

Shows current project status and available artifacts.

**Syntax:**
```bash
python3 scripts/zmanager.py status
```

**Examples:**
```bash
# Show project status
python3 scripts/zmanager.py status
```

**Output includes:**
- Project version (from VERSION.txt)
- Build directory status (JavaScript, Android)
- Tool availability (Gradle, npm)
- GitHub credentials status

### `info` Command

Displays detailed information about packages and artifacts.

**Syntax:**
```bash
python3 scripts/zmanager.py info [OPTIONS]
```

**Options:**
- `--user USER`: GitHub username/organization
- `--token TOKEN`: GitHub access token

**Examples:**
```bash
# Show detailed package information
python3 scripts/zmanager.py info

# With custom credentials
python3 scripts/zmanager.py info --user Zernikalos --token TOKEN
```

**Output includes:**
- NPM package information
- Maven artifact information
- Publication coordinates
- Package versions

## Prerequisites

### General Requirements

- **Python 3.6+** installed
- **Git** available and configured (for version commands)
- **Gradle wrapper** (`gradlew`) available and executable (for version/publish)
- **npm** installed and configured (for NPM publishing)

### GitHub Credentials

Authentication can be provided in several ways:

1. **Environment variables** (recommended):
   ```bash
   export GITHUB_ACTOR="Zernikalos"  # or GITHUB_USER
   export GITHUB_TOKEN="ghp_xxxxxxxxxxxxxxxx"
   ```

2. **Command line arguments**:
   ```bash
   python3 scripts/zmanager.py publish --all --user Zernikalos --token TOKEN
   ```

3. **Interactive prompt**: Token will be prompted if not provided

**Token Requirements:**
- Must have `packages:write` permission
- Personal access token (PAT) or GitHub App token
- Never commit tokens in code

## Published Artifacts

### NPM Packages

- **Location**: `https://npm.pkg.github.com/@zernikalos/*`
- **Registry**: GitHub Packages NPM Registry
- **Package**: `@zernikalos/zernikalos`
- **Authentication**: Via environment variables (no `.npmrc` files)

### Maven Artifacts

- **Location**: `https://maven.pkg.github.com/Zernikalos/Zernikalos`
- **Repository**: GitHub Packages Maven Repository
- **Publications**:
  - `dev.zernikalos:zernikalos` (Android)
  - `dev.zernikalos:zernikalos-js` (JavaScript/Kotlin)
  - Debug and Release variants

## Advantages

### Unified Interface
- ✅ **Single entry point** for all operations
- ✅ **Consistent command structure** across all features
- ✅ **Better maintainability** with modular architecture

### Version Management
- ✅ **Auto-versioning** from Conventional Commits
- ✅ **Version validation** prevents conflicts
- ✅ **Local testing** with `--no-push` option
- ✅ **Show next version** without creating it

### Publishing
- ✅ **Secure authentication** via environment variables (no `.npmrc` files)
- ✅ **Workspace support** for efficient NPM publishing
- ✅ **Automatic filtering** excludes test packages
- ✅ **Selective publishing** (NPM, Maven, or both)
- ✅ **Better error handling** and reporting

### Release Workflow
- ✅ **Complete automation** combining versioning and publishing
- ✅ **Auto-versioning** support in release command
- ✅ **Flexible options** (version only, publish only, or both)

## Common Usage Examples

### Version Management

```bash
# Create a new version
python3 scripts/zmanager.py version 1.2.3

# Auto-calculate version from Conventional Commits
python3 scripts/zmanager.py version --auto

# Preview next version without creating it
python3 scripts/zmanager.py version --show-next

# Create local version (no push, no CI/CD trigger)
python3 scripts/zmanager.py version 1.2.3 --no-push

# Use environment variable credentials
export GITHUB_ACTOR="Zernikalos"
export GITHUB_TOKEN="ghp_xxxxxxxxxxxxxxxx"
python3 scripts/zmanager.py version 1.2.3
```

### Publishing

```bash
# Publish all artifacts
python3 scripts/zmanager.py publish --all

# Publish only NPM packages
python3 scripts/zmanager.py publish --npm

# Publish only Maven artifacts
python3 scripts/zmanager.py publish --maven

# Use environment variable credentials
export GITHUB_ACTOR="Zernikalos"
export GITHUB_TOKEN="ghp_xxxxxxxxxxxxxxxx"
python3 scripts/zmanager.py publish --all
```

### Complete Release

```bash
# Full release (version + publish)
python3 scripts/zmanager.py release 1.2.3

# Auto-versioned release
python3 scripts/zmanager.py release --auto

# Version only (skip publish)
python3 scripts/zmanager.py release 1.2.3 --no-publish

# Use environment variable credentials
export GITHUB_ACTOR="Zernikalos"
export GITHUB_TOKEN="ghp_xxxxxxxxxxxxxxxx"
python3 scripts/zmanager.py release 1.2.3
```

### Status and Information

```bash
# Check project status
python3 scripts/zmanager.py status

# View detailed package information
python3 scripts/zmanager.py info
```

## Notes

- The Zernikalos Manager uses a modular architecture for better maintainability
- All commands handle user interruptions gracefully (Ctrl+C)
- Terminal colors and formatting are consistent across all commands
- Maven publishing targets GitHub Packages Maven Repository (not NPM)
- Maven artifacts include Debug and Release libraries for development and production
- **Auto-versioning** calculates versions from Conventional Commits when using `--auto`
- **Local testing** is supported with `--no-push` flag for version commands
- **Selective publishing** allows publishing only NPM or only Maven artifacts
- **Secure authentication**: No `.npmrc` files, uses environment variables
- **Workspace support**: Efficient NPM publishing with automatic filtering
- All commands validate prerequisites before execution
- Comprehensive error messages help identify and resolve issues quickly

## Related Documentation

- [Release Process](release-process.md) - Complete release workflow
- [Gradle Tasks](gradle-tasks.md) - Manual Gradle task documentation
