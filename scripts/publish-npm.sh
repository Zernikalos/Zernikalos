#!/bin/bash

# Zernikalos NPM Publish Script
# Publishes NPM packages to GitHub Packages

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

# Function to check if npm is available
check_npm() {
    if ! command -v npm &> /dev/null; then
        print_error "npm is not installed or not in PATH"
        print_error "Please install Node.js and npm first"
        exit 1
    fi
    
    print_success "npm version: $(npm --version)"
}

# Function to get GitHub credentials
get_github_credentials() {
    # Set default user if not provided
    if [[ -z $GITHUB_USER ]]; then
        GITHUB_USER="Zernikalos"
        print_status "Using default GitHub organization: $GITHUB_USER"
    fi
    
    # Check if credentials are provided via parameters
    if [[ -n $GITHUB_USER && -n $GITHUB_TOKEN ]]; then
        print_success "Using credentials from environment variables"
        return 0
    fi
    
    # Check if credentials are provided via command line
    if [[ -n $github_user && -n $github_token ]]; then
        print_success "Using credentials from command line parameters"
        GITHUB_USER="$github_user"
        GITHUB_TOKEN="$github_token"
        return 0
    fi
    
    # Prompt for credentials (only token, user is default)
    print_status "GitHub credentials required for .npmrc generation"
    print_status "Organization: $GITHUB_USER (default)"
    
    if [[ -z $GITHUB_TOKEN ]]; then
        read -s -p "Enter GitHub access token: " GITHUB_TOKEN
        echo
        if [[ -z $GITHUB_TOKEN ]]; then
            print_error "GitHub access token is required"
            exit 1
        fi
    fi
}

# Function to generate .npmrc file
generate_npmrc() {
    print_status "Generating .npmrc file with credentials..."
    
    # Get credentials if not already set
    get_github_credentials
    
    # Execute gradle command with credentials
    if ./gradlew generateNpmrc -Puser="$GITHUB_USER" -Paccess_token="$GITHUB_TOKEN"; then
        print_success ".npmrc file generated successfully"
    else
        print_error "Failed to generate .npmrc file"
        exit 1
    fi
}

# Function to build packages
build_packages() {
    print_status "Building packages with webpack..."
    
    if ./gradlew jsBrowserProductionWebpack; then
        print_success "Packages built successfully"
    else
        print_error "Failed to build packages"
        exit 1
    fi
}

# Function to check if build directory exists
check_build_directory() {
    if [[ ! -d "build/js" ]]; then
        print_warning "Build directory 'build/js' does not exist"
        print_status "Building packages automatically..."
        build_packages
    fi
}

# Function to check if .npmrc exists
check_npmrc() {
    if [[ ! -f "build/js/.npmrc" ]]; then
        print_warning ".npmrc file not found in build/js/"
        print_status "Generating .npmrc automatically..."
        generate_npmrc
    fi
    
    print_success ".npmrc file found and configured"
}

# Function to list available packages
list_packages() {
    print_status "Available packages in build/js/packages/@zernikalos/:"
    
    if [[ -d "build/js/packages/@zernikalos" ]]; then
        for package in build/js/packages/@zernikalos/*/; do
            if [[ -d "$package" ]]; then
                package_name=$(basename "$package")
                if [[ -f "$package/package.json" ]]; then
                    version=$(grep '"version"' "$package/package.json" | sed 's/.*"version": "\([^"]*\)".*/\1/')
                    echo "  - $package_name (v$version)"
                fi
            fi
        done
    else
        print_error "No packages found in build/js/packages/@zernikalos/"
        exit 1
    fi
}

# Function to publish a specific package
publish_package() {
    local package_name=$1
    local package_path="build/js/packages/@zernikalos/$package_name"
    
    if [[ ! -d "$package_path" ]]; then
        print_error "Package '$package_name' not found"
        exit 1
    fi
    
    if [[ ! -f "$package_path/package.json" ]]; then
        print_error "package.json not found in $package_path"
        exit 1
    fi
    
    print_status "Publishing package: @zernikalos/$package_name"
    
    # Change to the package directory
    cd "$package_path"
    
    # Check if package is already published
    local current_version=$(grep '"version"' package.json | sed 's/.*"version": "\([^"]*\)".*/\1/')
    print_status "Package version: $current_version"
    
    # Confirm publication
    read -p "Proceed with publishing @zernikalos/$package_name v$current_version? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_warning "Publication cancelled"
        cd - > /dev/null
        exit 0
    fi
    
    # Publish the package
    print_status "Publishing to GitHub Packages..."
    if npm publish; then
        print_success "Package @zernikalos/$package_name v$current_version published successfully!"
        print_status "Package available at: https://npm.pkg.github.com/@zernikalos/$package_name"
    else
        print_error "Failed to publish package"
        cd - > /dev/null
        exit 1
    fi
    
    # Return to original directory
    cd - > /dev/null
}

# Function to publish all packages
publish_all_packages() {
    print_status "Publishing all packages..."
    
    for package in build/js/packages/@zernikalos/*/; do
        if [[ -d "$package" ]]; then
            package_name=$(basename "$package")
            if [[ -f "$package/package.json" ]]; then
                echo
                publish_package "$package_name"
            fi
        fi
    done
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS] [PACKAGE_NAME]"
    echo
    echo "Options:"
    echo "  -h, --help           Show this help message"
    echo "  -a, --all            Publish all packages"
    echo "  -l, --list           List available packages"
    echo "  -u, --user USER      GitHub organization/user"
    echo "  -t, --token TOKEN    GitHub access token"
    echo
    echo "Examples:"
    echo "  $0                                    # Show available packages"
    echo "  $0 -l                                 # List available packages"
    echo "  $0 -a                                 # Publish all packages"
    echo "  $0 zernikalos                         # Publish specific package (user defaults to Zernikalos)"
    echo "  $0 -u Zernikalos -t TOKEN zernikalos # With custom credentials"
    echo "  $0 -t TOKEN zernikalos                # With custom token, default user (Zernikalos)"
    echo
    echo "Prerequisites:"
    echo "  1. Ensure you have npm installed and configured"
    echo "  2. GitHub credentials (one of these methods):"
    echo "     - Environment variables: GITHUB_USER and GITHUB_TOKEN"
    echo "     - Command line: -u USER -t TOKEN"
    echo "     - Interactive prompt (only token required, user defaults to Zernikalos)"
    echo "  3. The script will automatically:"
    echo "     - Generate .npmrc with credentials"
    echo "     - Build packages with webpack"
    echo "     - Publish packages to GitHub Packages"
}

# Main function
main() {
    local action="list"
    local package_name=""
    local github_user=""
    local github_token=""
    
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                show_usage
                exit 0
                ;;
            -a|--all)
                action="all"
                shift
                ;;
            -l|--list)
                action="list"
                shift
                ;;
            -u|--user)
                github_user="$2"
                shift 2
                ;;
            -t|--token)
                github_token="$2"
                shift 2
                ;;
            -*)
                print_error "Unknown option: $1"
                show_usage
                exit 1
                ;;
            *)
                if [[ -z $package_name ]]; then
                    package_name=$1
                    action="single"
                else
                    print_error "Multiple package names specified"
                    exit 1
                fi
                shift
                ;;
        esac
    done
    
    # Check prerequisites
    check_directory
    check_npm
    
    # Auto-generate and build if needed
    check_npmrc
    check_build_directory
    
    # Execute action
    case $action in
        "list")
            list_packages
            echo
            print_status "To publish a specific package: $0 <package_name>"
            print_status "To publish all packages: $0 -a"
            ;;
        "single")
            if [[ -z $package_name ]]; then
                print_error "Package name required"
                show_usage
                exit 1
            fi
            publish_package "$package_name"
            ;;
        "all")
            publish_all_packages
            ;;
    esac
}

# Check if script is being sourced
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
