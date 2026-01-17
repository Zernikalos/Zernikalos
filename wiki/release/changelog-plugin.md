# Changelog Plugin Integration

This document describes the integration of the [JetBrains Gradle Changelog Plugin](https://github.com/JetBrains/gradle-changelog-plugin) into the Zernikalos project.

## Overview

The Changelog Plugin automatically manages the `CHANGELOG.md` file in the "keep a changelog" style, integrating with the release process to automatically update changelog entries when creating new versions.

## Configuration

The plugin is configured in `build.gradle.kts` with the following settings:

```kotlin
changelog {
    version.set(provider { zernikalosVersion })
    path = file("CHANGELOG.md").canonicalPath
    header = provider { "[${version.get()}] - ${date()}" }
    headerParserRegex = """(\d+\.\d+\.\d+)""".toRegex()
    repositoryUrl = "https://github.com/Zernikalos/Zernikalos"
    // ... other settings
}
```

**Key Features:**
- Automatically syncs with `VERSION.txt` via `zernikalosVersion`
- Generates links to GitHub releases
- Follows "Keep a Changelog" format
- Supports Semantic Versioning

## Integration with Release Process

The changelog plugin is automatically integrated into the release workflow:

1. **Manual Gradle Release**: When running `./gradlew releaseCommit`, the changelog is automatically patched before creating the commit
2. **Zernikalos Manager**: When using `python3 scripts/zmanager.py release`, the changelog is handled through Gradle tasks

### Release Flow with Changelog

```
setVersion → updateVersion → patchChangelog → releaseCommit
     ↓             ↓                ↓              ↓
  VERSION.txt  Generate files   Update CHANGELOG  Git commit & tag
```

## Available Tasks

### `initializeChangelog`

Creates a new `CHANGELOG.md` file with an unreleased section and empty groups.

**Usage:**
```bash
./gradlew initializeChangelog
```

### `patchChangelog`

Updates the unreleased section with the current version. This task runs automatically as part of `releaseCommit`.

**Usage:**
```bash
./gradlew patchChangelog
```

**Options:**
- `--release-note`: Use custom release note instead of unreleased section content

**Example:**
```bash
./gradlew patchChangelog --release-note="- Custom release notes"
```

### `getChangelog`

Retrieves changelog content for a specific version or the unreleased section.

**Usage:**
```bash
# Get unreleased section
./gradlew getChangelog --unreleased

# Get specific version
./gradlew getChangelog --project-version=1.2.3

# Get without header
./gradlew getChangelog --unreleased --no-header

# Write to file
./gradlew getChangelog --unreleased --output-file=build/changelog.md
```

## Changelog Format

The changelog follows the "Keep a Changelog" format:

```markdown
# Changelog

## [Unreleased]
### Added
- New features

### Changed
- Changes to existing functionality

### Fixed
- Bug fixes

## [1.0.0] - 2024-01-01
### Added
- Initial release

[1.0.0]: https://github.com/Zernikalos/Zernikalos/releases/tag/v1.0.0
```

## Working with Changelog

### Adding Changes

Edit `CHANGELOG.md` directly under the `[Unreleased]` section:

```markdown
## [Unreleased]
### Added
- New rendering feature
- Performance improvements

### Fixed
- Memory leak in texture handling
```

### Creating a Release

When you create a release, the changelog is automatically patched:

```bash
# Using Zernikalos Manager (recommended)
python3 scripts/zmanager.py release 1.2.3

# Or using Gradle directly
./gradlew setVersion -PnewVersion=1.2.3
./gradlew releaseCommit
```

This will:
1. Move all `[Unreleased]` entries to `[1.2.3]`
2. Add the release date
3. Create a link to the GitHub release
4. Add a new empty `[Unreleased]` section

### Accessing Changelog Content Programmatically

You can access changelog content in Gradle tasks:

```kotlin
tasks.register("myTask") {
    doLast {
        val unreleased = changelog.getUnreleased()
        val htmlContent = changelog.renderItem(unreleased, Changelog.OutputType.HTML)
        // Use the HTML content...
    }
}
```

## Integration Examples

### Using Changelog in Documentation

You can generate release notes from the changelog:

```kotlin
tasks.register("generateReleaseNotes") {
    doLast {
        val latest = changelog.getLatest()
        val notes = changelog.renderItem(latest, Changelog.OutputType.HTML)
        file("build/release-notes.html").writeText(notes)
    }
}
```

### Custom Release Notes

If you need to override the unreleased content:

```bash
./gradlew patchChangelog --release-note="- Major performance improvements
- New API features
- Security fixes"
```

## Best Practices

1. **Update Unreleased Section Regularly**: Add changes as you develop, not at release time
2. **Follow Semantic Versioning**: Use proper version numbers (MAJOR.MINOR.PATCH)
3. **Use Clear Descriptions**: Write clear, concise change descriptions
4. **Group Changes Properly**: Use the standard groups (Added, Changed, Deprecated, Removed, Fixed, Security)
5. **Link Related Issues**: Reference GitHub issues or PRs when relevant

## Related Documentation

- [Release Process](release-process.md) - Complete release workflow
- [Gradle Tasks](gradle-tasks.md) - Manual Gradle task documentation
- [Zernikalos Manager Reference](zmanager-reference.md) - Automated release tool
- [Keep a Changelog](https://keepachangelog.com/) - Changelog format specification
- [JetBrains Changelog Plugin](https://github.com/JetBrains/gradle-changelog-plugin) - Official plugin documentation
