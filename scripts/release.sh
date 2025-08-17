#!/bin/bash

# Zernikalos Release Script
# Automates the complete release process for X.Y.Z versions

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if we're in the right directory
check_directory() {
    if [[ ! -f "build.gradle.kts" ]]; then
        print_error "This script must be run from the Zernikalos project root directory"
        exit 1
    fi
}

# Function to validate version format
validate_version() {
    local version=$1
    if [[ ! $version =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
        print_error "Invalid version format. Use X.Y.Z (e.g., 0.3.0)"
        exit 1
    fi
}

# Function to check if git is clean
check_git_status() {
    if [[ -n $(git status --porcelain) ]]; then
        print_warning "Git working directory is not clean. Please commit or stash changes first."
        git status --short
        read -p "Continue anyway? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            print_error "Release cancelled"
            exit 1
        fi
    fi
}

# Function to check if version already exists
check_version_exists() {
    local version=$1
    if git tag -l | grep -q "v$version"; then
        print_error "Version v$version already exists as a tag"
        exit 1
    fi
}

# Function to confirm release
confirm_release() {
    local version=$1
    local no_push=$2
    local current_version=$(cat VERSION.txt)
    
    echo
    print_status "Current version: $current_version"
    print_status "New version: $version"
    echo
    print_warning "This will:"
    echo "  1. Update VERSION.txt to $version"
    echo "  2. Generate version constants"
    echo "  3. Create release commit"
    echo "  4. Create git tag v$version"
    
    if [[ $no_push == true ]]; then
        echo "  5. [SKIP] Push changes (--no-push flag detected)"
        print_warning "This is a LOCAL release only. No CI/CD will be triggered."
    else
        echo "  5. Push changes and tag to trigger CI/CD"
    fi
    echo
    
    read -p "Proceed with release? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_error "Release cancelled"
        exit 1
    fi
}

# Function to execute release steps
execute_release() {
    local version=$1
    
    print_status "Starting release process for version $version..."
    
    # Step 1: Set version
    print_status "Setting version to $version..."
    ./gradlew setVersion -PnewVersion=$version
    
    # Step 2: Generate version files
    print_status "Generating version-dependent files..."
    ./gradlew updateVersion
    
    # Step 3: Create release commit and tag
    print_status "Creating release commit and tag..."
    ./gradlew releaseCommit
    
    print_success "Release files generated successfully!"
}

# Function to push changes
push_release() {
    local version=$1
    
    print_status "Pushing changes to remote repository..."
    
    # Push main branch
    git push origin main
    
    # Push tag
    git push origin v$version
    
    print_success "Changes pushed successfully!"
    print_success "CI/CD pipeline will now build and publish version $version"
}

# Function to show next steps
show_next_steps() {
    local version=$1
    
    echo
    print_success "🎉 Release v$version completed successfully!"
    echo
    print_status "Next steps:"
    echo "  1. Monitor the GitHub Actions workflow:"
    echo "     https://github.com/Zernikalos/Zernikalos/actions"
    echo "  2. Check the release build:"
    echo "     https://github.com/Zernikalos/Zernikalos/actions/workflows/build.yml"
    echo "  3. Verify packages are published:"
    echo "     - Maven: https://maven.pkg.github.com/Zernikalos/Zernikalos"
    echo "     - NPM: https://npm.pkg.github.com/@zernikalos/zernikalos"
    echo
    print_status "The release will be automatically published once the build completes."
}

# Function to show local release information
show_local_release_info() {
    local version=$1
    
    echo
    print_success "🎉 Local Release v$version completed successfully!"
    echo
    print_status "This was a LOCAL release (--no-push flag used)."
    print_status "To publish this release to GitHub:"
    echo
    echo "  1. Review the changes:"
    echo "     git log --oneline -5"
    echo "     git show v$version"
    echo
    echo "  2. Push when ready:"
    echo "     git push origin main"
    echo "     git push origin v$version"
    echo
    print_warning "No CI/CD pipeline was triggered. This is just a local preparation."
}

# Main function
main() {
    local version=""
    local no_push=false
    
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            --no-push)
                no_push=true
                shift
                ;;
            -*)
                print_error "Unknown option: $1"
                print_error "Usage: $0 <version> [--no-push]"
                print_error "Example: $0 0.3.0 --no-push"
                exit 1
                ;;
            *)
                if [[ -z $version ]]; then
                    version=$1
                else
                    print_error "Multiple versions specified: $version and $1"
                    exit 1
                fi
                shift
                ;;
        esac
    done
    
    # Check if version was provided
    if [[ -z $version ]]; then
        print_error "Usage: $0 <version> [--no-push]"
        print_error "Example: $0 0.3.0"
        print_error "Example: $0 0.3.0 --no-push"
        exit 1
    fi
    
    # Validate version format
    validate_version $version
    
    # Check we're in the right directory
    check_directory
    
    # Check git status
    check_git_status
    
    # Check if version already exists
    check_version_exists $version
    
    # Confirm release
    confirm_release $version $no_push
    
    # Execute release
    execute_release $version
    
    # Push changes (if not --no-push)
    if [[ $no_push == false ]]; then
        push_release $version
        show_next_steps $version
    else
        show_local_release_info $version
    fi
}

# Check if script is being sourced
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
