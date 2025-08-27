# Zernikalos Manual Publish Scripts

This directory contains scripts for publishing Zernikalos packages to GitHub Packages.

## Available Scripts

### 1. `publish-npm.sh` (Bash)
Original Bash script for publishing NPM packages.

### 2. `publish-npm.py` (Python)
Python version of the bash script with equivalent functionality.

### 3. `publish-android.py` (Python)
Python script for publishing Android artifacts to GitHub Packages Maven Repository.

### 4. `publish-all.py` (Python)
Combined script that publishes both NPM packages and Android artifacts in a single command.

## Features

### NPM Scripts (publish-npm.sh and publish-npm.py):
- ✅ Automatic `.npmrc` generation with GitHub credentials
- ✅ Automatic package building with webpack
- ✅ Individual or all package publishing
- ✅ Prerequisites verification (npm, correct directory)
- ✅ Terminal colors for better readability
- ✅ Robust error handling

### Android Script (publish-android.py):
- ✅ Android Debug and Release artifacts publishing
- ✅ Publishing of ALL publications with `publishAllPublicationsToMavenRepository`
- ✅ Automatic project building if necessary
- ✅ Publishing to GitHub Packages Maven Repository
- ✅ Detailed Maven coordinates information
- ✅ Interactive and command line modes

### Combined Script (publish-all.py):
- ✅ Complete publishing of all artifacts
- ✅ Automatic orchestration of NPM and Android scripts
- ✅ Complete project status view
- ✅ Options to publish everything or separately
- ✅ Detailed results summary

## Usage

### Bash Script
```bash
# List available packages
./publish-npm.sh -l

# Publish all packages
./publish-npm.sh -a

# Publish specific package
./publish-npm.sh zernikalos

# With custom credentials
./publish-npm.sh -u Zernikalos -t TOKEN zernikalos
```

### Python NPM Script
```bash
# List available packages
python3 publish-npm.py -l

# Publish all packages
python3 publish-npm.py -a

# Publish specific package
python3 publish-npm.py zernikalos

# With custom credentials
python3 publish-npm.py -u Zernikalos -t TOKEN zernikalos
```

### Python Android Script
```bash
# Interactive mode
python3 publish-android.py

# Show publication information
python3 publish-android.py -i

# Publish Debug artifacts
python3 publish-android.py -d

# Publish Release artifacts
python3 publish-android.py -r

# Publish all Android artifacts (Debug + Release)
python3 publish-android.py -a

# Publish ALL publications (recommended)
python3 publish-android.py --all-publications

# With custom credentials
python3 publish-android.py -u Zernikalos -t TOKEN --all-publications
```

### Combined Script
```bash
# Interactive mode
python3 publish-all.py

# Show project status
python3 publish-all.py -s

# Show detailed package information
python3 publish-all.py -i

# Publish only NPM packages
python3 publish-all.py -n

# Publish only Android artifacts
python3 publish-all.py -a

# Publish everything (NPM + Android)
python3 publish-all.py --all

# With custom credentials
python3 publish-all.py -u Zernikalos -t TOKEN --all
```

## Prerequisites

### For NPM scripts:
1. **npm**: Installed and configured
2. **GitHub credentials**: One of these options:
   - Environment variables: `GITHUB_USER` and `GITHUB_TOKEN`
   - Command line: `-u USER -t TOKEN`
   - Interactive prompt (token only, default user: Zernikalos)

### For Android script:
1. **Gradle wrapper**: `gradlew` available and executable
2. **GitHub credentials**: One of these options:
   - Environment variables: `GITHUB_ACTOR` and `GITHUB_TOKEN`
   - Command line: `-u USER -t TOKEN`
   - Interactive prompt (token only, default user: Zernikalos)

### For combined script:
1. **Dependent scripts**: `publish-npm.py` and `publish-android.py` must be available
2. **GitHub credentials**: Same requirements as individual scripts
3. **Direct import**: Uses Python functions instead of subprocess

### For Python scripts:
- Python 3.6+ installed

## Advantages of each version

### Bash (`publish-npm.sh`)
- ✅ No Python installation required
- ✅ Faster on Unix/Linux systems
- ✅ Familiar syntax for shell users

### Python NPM (`publish-npm.py`)
- ✅ Better error handling
- ✅ More robust arguments with `argparse`
- ✅ Greater portability across systems
- ✅ More maintainable and extensible code
- ✅ Better handling of interruptions (Ctrl+C)

### Python Android (`publish-android.py`)
- ✅ Specific publishing for Android artifacts
- ✅ Complete publishing with `publishAllPublicationsToMavenRepository`
- ✅ Detailed Maven coordinates information
- ✅ Interactive mode for easier use
- ✅ Automatic project building
- ✅ Publishing to GitHub Packages Maven Repository

### Python Combined (`publish-all.py`)
- ✅ Complete publishing of all artifacts
- ✅ Automatic orchestration of NPM and Android scripts
- ✅ Complete project status view
- ✅ Options to publish everything or separately
- ✅ Detailed results summary

## Automatic Operation

### NPM scripts automatically:
1. Verify you are in the project root directory
2. Check that npm is installed
3. Generate `.npmrc` if it doesn't exist
4. Build packages if `build/js` directory doesn't exist
5. Publish according to selected option

### Android script automatically:
1. Verify you are in the project root directory
2. Check that Gradle wrapper is available
3. Build the project if `build` directory doesn't exist
4. Publish Android artifacts to GitHub Packages Maven Repository

### Combined script automatically:
1. Verify you are in the project root directory
2. Directly import functions from other scripts
3. Orchestrate NPM and Android publishing in sequence
4. Provide complete results summary

## Common Usage Examples

### NPM Scripts:
```bash
# Just see what packages are available
python3 publish-npm.py

# Publish a specific package (with confirmation prompt)
python3 publish-npm.py zernikalos

# Publish all packages at once
python3 publish-npm.py -a

# Use environment variable credentials
export GITHUB_USER="Zernikalos"
export GITHUB_TOKEN="ghp_xxxxxxxxxxxxxxxx"
python3 publish-npm.py zernikalos
```

### Android Script:
```bash
# Interactive mode to choose what to publish
python3 publish-android.py

# See Maven coordinates information
python3 publish-android.py -i

# Publish only Debug artifacts
python3 publish-android.py -d

# Publish only Release artifacts
python3 publish-android.py -r

# Publish all artifacts at once
python3 publish-android.py -a

# Use environment variable credentials
export GITHUB_ACTOR="Zernikalos"
export GITHUB_TOKEN="ghp_xxxxxxxxxxxxxxxx"
python3 publish-android.py -a
```

### Combined Script:
```bash
# Interactive mode to choose what to publish
python3 publish-all.py

# See complete project status
python3 publish-all.py -s

# See detailed package information
python3 publish-all.py -i

# Publish only NPM packages
python3 publish-all.py -n

# Publish only Android artifacts (Debug + Release)
python3 publish-all.py -a

# Publish ALL Android publications (recommended)
python3 publish-all.py --all-publications

# Publish everything at once
python3 publish-all.py --all

# Use environment variable credentials
export GITHUB_ACTOR="Zernikalos"
export GITHUB_TOKEN="ghp_xxxxxxxxxxxxxxxx"
python3 publish-all.py --all
```

## Published Artifacts

### NPM Scripts:
- **JavaScript Packages**: Published to GitHub Packages NPM Registry
- **Location**: `https://npm.pkg.github.com/@zernikalos/*`

### Android Script:
- **Android Debug Artifacts**: Libraries for development
- **Android Release Artifacts**: Libraries for production
- **All Publications**: `publishAllPublicationsToMavenRepository` (recommended)
  - `dev.zernikalos:zernikalos` (Android)
  - `dev.zernikalos:zernikalos-js` (JavaScript/Kotlin)
  - Any other configured publication
- **Location**: `https://maven.pkg.github.com/Zernikalos/Zernikalos`

## Notes

- Python scripts are complete rewrites of bash scripts, maintaining all functionality
- All scripts can coexist in the same directory
- Python scripts handle user interruptions better (Ctrl+C)
- Terminal colors work the same in all versions
- Android script publishes to GitHub Packages Maven Repository (not NPM)
- Android artifacts include Debug and Release libraries for development and production
- **New functionality**: `--all-publications` to publish everything with a single command
- Combined script orchestrates both publishing processes automatically
- All scripts are compatible with each other and can be used independently
- Combined script uses direct function import instead of subprocess
- Better performance and error handling by avoiding external calls
