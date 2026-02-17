# Changelog Plugin Integration

This document describes the integration of the [git-changelog Gradle Plugin](https://github.com/tomasbjerre/git-changelog-gradle-plugin) (se.bjurr.gitchangelog) into the Zernikalos project.

## Overview

The git-changelog plugin generates `CHANGELOG.md` from git commits and tags. It uses a Mustache template to format the output, supporting Conventional Commits (feat, fix, perf, refactor, docs, etc.). The plugin is configured in the `buildLogic` convention plugin and integrates automatically with the release process.

## Configuration

The plugin is configured in `buildLogic/src/main/kotlin/zernikalos.release-conventions.gradle.kts`:

- **Output file**: `CHANGELOG.md` at project root
- **Template**: `.changelog.template` at project root
- **Repository**: Uses `githubOwner` and `githubRepo` from constants for GitHub links

## Integration with Release Process

The changelog is automatically regenerated as part of `releaseCommit`:

1. **Manual Gradle Release**: When running `./gradlew releaseCommit`, the changelog is regenerated after the commit and before the tag is finalized
2. **Zernikalos Manager**: When using `python3 scripts/zmanager.py release`, the changelog is handled through the Gradle `releaseCommit` task

### Release Flow with Changelog

```
setVersion → updateVersion → releaseCommit
     ↓             ↓              ↓
  VERSION.txt  Generate files   Git commit → gitChangelog → amend with CHANGELOG
```

The `releaseCommit` task:
1. Stages all changes and creates the release commit
2. Creates the version tag
3. Runs `gitChangelog` to regenerate `CHANGELOG.md` with the new tag
4. Amends the commit to include the updated changelog
5. Recreates the tag

## Available Tasks

### `gitChangelog`

Generates `CHANGELOG.md` from git history using the configured template.

**Usage:**
```bash
./gradlew :engine:gitChangelog
```

**Behavior:**
- Reads git commits and tags from the repository root
- Uses `.changelog.template` for output format
- Writes to `CHANGELOG.md`
- Runs automatically as part of `releaseCommit`

## Changelog Format

The changelog is generated from the Mustache template (`.changelog.template`) and groups commits by tag and by type:

- **feat** → Added
- **fix** → Fixed
- **perf** → Performance
- **refactor** → Changed
- **docs** → Documentation
- **style** → Style
- **test** → Tests
- **build** → Build
- **ci** → CI/CD
- **chore** → Chore

Each entry includes the commit hash linked to GitHub.

## Customizing the Template

Edit `.changelog.template` at the project root. The template uses Mustache syntax with placeholders:

- `{{#tags}}` / `{{/tags}}` – iterate over git tags
- `{{name}}` – tag name (e.g., v1.0.0)
- `{{#commits}}` / `{{/commits}}` – iterate over commits
- `{{#ifCommitType . type='feat'}}` – filter by Conventional Commit type
- `{{{commitDescription .}}}` – commit message
- `{{hash}}` – short hash, `{{hashFull}}` – full hash

Placeholders `__GITHUB_OWNER__` and `__GITHUB_REPO__` are replaced at runtime with values from `buildLogic`.

## Best Practices

1. **Follow Conventional Commits**: Use `feat:`, `fix:`, `docs:`, etc. so commits are categorized correctly
2. **Use Scopes**: e.g. `feat(render): add new shader` for better changelog organization
3. **Write Clear Descriptions**: Keep commit messages concise and descriptive
4. **Link Related Issues**: Reference GitHub issues when relevant (links are preserved in the changelog)

## Related Documentation

- [Release Process](release-process.md) - Complete release workflow
- [Gradle Tasks](gradle-tasks.md) - Manual Gradle task documentation
- [Zernikalos Manager Reference](zmanager-reference.md) - Automated release tool
- [git-changelog Plugin](https://github.com/tomasbjerre/git-changelog-gradle-plugin) - Official plugin documentation
